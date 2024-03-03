package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.antlr.parser.stellaParserVisitor
import dev.ebelekhov.typechecker.errors.*
import dev.ebelekhov.typechecker.types.*
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode

class StellaVisitor(private val funcContext: FuncContext = FuncContext())
    : stellaParserVisitor<Type> {
    override fun visit(tree: ParseTree?): Type {
        TODO("Not yet implemented")
    }

    override fun visitChildren(node: RuleNode?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTerminal(node: TerminalNode?): Type {
        TODO("Not yet implemented")
    }

    override fun visitErrorNode(node: ErrorNode?): Type {
        TODO("Not yet implemented")
    }

    override fun visitStart_Program(ctx: stellaParser.Start_ProgramContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitStart_Expr(ctx: stellaParser.Start_ExprContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitStart_Type(ctx: stellaParser.Start_TypeContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitProgram(ctx: stellaParser.ProgramContext): Type {
        var programType : Type? = null

        ctx.decls.forEach {
            val type = it.accept(this)
            if (it is stellaParser.DeclFunContext &&
                it.name.text == "main") {
                programType = type
            }
        }

        if (programType == null) throw ExitException(MissingMainError())

        val mainArgsSize = (programType as FuncType).argTypes.size
        if (mainArgsSize != 1) {
            throw ExitException(IncorrectArityOfMainError(mainArgsSize))
        }

        return programType!!
    }

    override fun visitLanguageCore(ctx: stellaParser.LanguageCoreContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitAnExtension(ctx: stellaParser.AnExtensionContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitDeclFun(ctx: stellaParser.DeclFunContext): Type {
        val params = ctx.paramDecls
        val paramsInfo = params.map { Pair(it.name.text, it.paramType.accept(this)) }
        val returnType = ctx.returnType.accept(this)
        val funcType = FuncType(paramsInfo.map { it.second }, returnType)

        funcContext.addVariable(ctx.name.text, funcType)
        funcContext.runWithVariables(paramsInfo) {
            val nestedFunctions = ctx.localDecls.filterIsInstance<stellaParser.DeclFunContext>().map {
                Pair(it.name.text, it.accept(this))
            }

            funcContext.runWithVariables(nestedFunctions){
                funcContext.runWithExpectedReturnType(returnType, ctx.returnExpr) {
                    ctx.returnExpr.accept(this)
                }
            }
        }

        return funcType
    }

    override fun visitDeclFunGeneric(ctx: stellaParser.DeclFunGenericContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitDeclTypeAlias(ctx: stellaParser.DeclTypeAliasContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitDeclExceptionType(ctx: stellaParser.DeclExceptionTypeContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitDeclExceptionVariant(ctx: stellaParser.DeclExceptionVariantContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitInlineAnnotation(ctx: stellaParser.InlineAnnotationContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitParamDecl(ctx: stellaParser.ParamDeclContext): Type {
        TODO("Not yet implemented")
    }

    override fun visitFold(ctx: stellaParser.FoldContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitAdd(ctx: stellaParser.AddContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitIsZero(ctx: stellaParser.IsZeroContext): Type {
        funcContext.runWithExpectedReturnType(NatType, ctx) { ctx.n.accept(this) }

        return BoolType
    }

    override fun visitVar(ctx: stellaParser.VarContext): Type {
        val name = ctx.name.text
        val varType = funcContext.getVariableType(name)

        return varType ?: throw ExitException(UndefinedVariableError(name, ctx.parent))
    }

    override fun visitTypeAbstraction(ctx: stellaParser.TypeAbstractionContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitDivide(ctx: stellaParser.DivideContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitLessThan(ctx: stellaParser.LessThanContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitDotRecord(ctx: stellaParser.DotRecordContext): Type {
        val exprType = funcContext.runWithoutExpectations { ctx.expr().accept(this) }
        val recordType = exprType.ensureOrError(RecordType::class) { NotARecordError(it, ctx) }

        val label = ctx.label.text
        if (!recordType.fields.containsKey(label)) throw ExitException(UnexpectedFieldAccessError(label, ctx))

        return recordType.fields[label]!!
    }

    override fun visitGreaterThan(ctx: stellaParser.GreaterThanContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitEqual(ctx: stellaParser.EqualContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitThrow(ctx: stellaParser.ThrowContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitMultiply(ctx: stellaParser.MultiplyContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitConstMemory(ctx: stellaParser.ConstMemoryContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitList(ctx: stellaParser.ListContext): Type {
        val listType = funcContext
            .getCurrentExpectedReturnType()
            ?.ensureOrError(ListType::class) { UnexpectedListError(ctx) }

        if (ctx.exprs.isEmpty()) {
            return listType ?: throw ExitException(AmbiguousListError(ctx))
        }

        val firstElemType =
            if (listType != null)
                funcContext.runWithExpectedReturnType(listType.type, ctx) { ctx.exprs.first().accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.exprs.first().accept(this) }

        ctx.exprs.forEach {
            funcContext.runWithExpectedReturnType(firstElemType, ctx) { it.accept(this) }
        }

        return ListType(firstElemType)
    }

    override fun visitTryCatch(ctx: stellaParser.TryCatchContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitHead(ctx: stellaParser.HeadContext): Type {
        return funcContext.runWithoutExpectations {
            ctx.list.accept(this).ensureOrError(ListType::class) { NotAListError(it, ctx) }.type
        }
    }

    override fun visitTerminatingSemicolon(ctx: stellaParser.TerminatingSemicolonContext): Type {
        return ctx.expr().accept(this)
    }

    override fun visitNotEqual(ctx: stellaParser.NotEqualContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitConstUnit(ctx: stellaParser.ConstUnitContext?): Type {
        return UnitType
    }

    override fun visitSequence(ctx: stellaParser.SequenceContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitConstFalse(ctx: stellaParser.ConstFalseContext?): Type {
        return BoolType
    }

    override fun visitAbstraction(ctx: stellaParser.AbstractionContext): Type {
        val expectedFuncType = funcContext
           .getCurrentExpectedReturnType()
            ?.ensureOrError(FuncType::class) { type -> UnexpectedLambdaError(type, ctx) }

        val params = ctx.paramDecls
        if (expectedFuncType != null && expectedFuncType.argTypes.size != params.size) {
            throw ExitException(UnexpectedNumberOfParametersInLambdaError(expectedFuncType.argTypes.size, ctx))
        }

        val paramTypes = params.mapIndexed { idx, value ->
            val type =
                if (expectedFuncType == null)
                    funcContext.runWithoutExpectations { value.stellatype().accept(this) }
                else
                    funcContext.runWithExpectedReturnType(expectedFuncType.argTypes[idx], ctx) {
                        value.stellatype().accept(this).ensureOrError(expectedFuncType.argTypes[idx]::class) {
                            UnexpectedTypeForParameterError(it, expectedFuncType.argTypes[idx], ctx)
                        }
                    }

            Pair(value.name.text, type)
        }

        return funcContext.runWithVariables(paramTypes) {
            val returnType =
                if (expectedFuncType?.returnType == null)
                    funcContext.runWithoutExpectations { ctx.returnExpr.accept(this) }
                else
                    funcContext.runWithExpectedReturnType(expectedFuncType.returnType, ctx) { ctx.returnExpr.accept(this) }

            FuncType(paramTypes.map { it.second }, returnType)
        }
    }

    override fun visitConstInt(ctx: stellaParser.ConstIntContext): Type {
        return NatType
    }

    override fun visitVariant(ctx: stellaParser.VariantContext): Type {
        val expectedVariantType = funcContext
            .getCurrentExpectedReturnType()
            ?.ensureOrError(VariantType::class) { UnexpectedVariantError(it, ctx) }
            ?: throw ExitException(AmbiguousVariantTypeError(ctx))

        val variantLabel = ctx.label.text
        if (!expectedVariantType.variants.containsKey(variantLabel)) {
            throw ExitException(UnexpectedVariantLabelError(
                variantLabel,
                expectedVariantType,
                ctx))
        }

        val expectedLabel = expectedVariantType.variants[variantLabel]
        if (expectedLabel == null && ctx.rhs != null) {
            throw ExitException(UnexpectedDataForNullaryLabelError(expectedVariantType, ctx))
        }
        if (expectedLabel != null && ctx.rhs == null) {
            throw ExitException(MissingDataForLabelError(expectedVariantType, ctx))
        }

        if (expectedLabel != null) {
            funcContext.runWithExpectedReturnType(expectedLabel, ctx) { ctx.rhs.accept(this) }
        }

        return expectedVariantType
    }

    override fun visitConstTrue(ctx: stellaParser.ConstTrueContext?): Type {
        return BoolType
    }

    override fun visitSubtract(ctx: stellaParser.SubtractContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeCast(ctx: stellaParser.TypeCastContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitIf(ctx: stellaParser.IfContext): Type {
        funcContext.runWithExpectedReturnType(BoolType, ctx) { ctx.condition.accept(this) }

        val thenType = ctx.thenExpr.accept(this)
        funcContext.runWithExpectedReturnType(thenType, ctx) { ctx.elseExpr.accept(this) }

        return thenType
    }

    override fun visitApplication(ctx: stellaParser.ApplicationContext): Type {
        val expectedFunType =
            funcContext
                .runWithoutExpectations { ctx.`fun`.accept(this) }
                .ensureOrError(FuncType::class) { NotAFunctionError(it, ctx.`fun`) }

        if (expectedFunType.argTypes.size != ctx.args.size) {
            throw ExitException(IncorrectNumberOfArgumentsError(ctx.args.size, expectedFunType.argTypes.size, ctx))
        }

        expectedFunType.argTypes.forEachIndexed { i, _ ->
            funcContext.runWithExpectedReturnType(expectedFunType.argTypes[i], ctx) {
                ctx.args[i].accept(this)
            }
        }

        return expectedFunType.returnType
    }

    override fun visitDeref(ctx: stellaParser.DerefContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitIsEmpty(ctx: stellaParser.IsEmptyContext): Type {
        funcContext.runWithoutExpectations {
            ctx.list.accept(this).ensureOrError(ListType::class) { NotAListError(it, ctx) }
        }

        return BoolType
    }

    override fun visitPanic(ctx: stellaParser.PanicContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitLessThanOrEqual(ctx: stellaParser.LessThanOrEqualContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitSucc(ctx: stellaParser.SuccContext): Type {
        return funcContext.runWithExpectedReturnType(NatType, ctx) { ctx.n.accept(this) }
    }

    override fun visitInl(ctx: stellaParser.InlContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()
            ?.ensureOrError(SumType::class) { UnexpectedInjectionError(it, ctx) }
            ?: throw ExitException(AmbiguousSumTypeError(ctx))

        funcContext.runWithExpectedReturnType(expectedType.inl, ctx) { ctx.expr().accept(this) }

        return expectedType
    }

    override fun visitGreaterThanOrEqual(ctx: stellaParser.GreaterThanOrEqualContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitInr(ctx: stellaParser.InrContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()
            ?.ensureOrError(SumType::class) { UnexpectedInjectionError(it, ctx) }
            ?: throw ExitException(AmbiguousSumTypeError(ctx))

        funcContext.runWithExpectedReturnType(expectedType.inr, ctx) { ctx.expr().accept(this) }

        return expectedType
    }

    override fun visitMatch(ctx: stellaParser.MatchContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()

        val matchCaseType = funcContext.runWithoutExpectations { ctx.expr().accept(this) }
        if (ctx.cases.isEmpty()) {
            throw ExitException(IllegalEmptyMatchingError(ctx))
        }

        val casesExprTypes = ctx.cases.map { case ->
            funcContext.runWithPatternVariable {
                funcContext.runWithPatternVariable {
                    funcContext.runWithExpectedReturnType(matchCaseType, ctx) {
                        case.pattern().accept(this).ensureOrError(matchCaseType) {
                            UnexpectedTypeForExpressionError(it, matchCaseType, ctx)
                        }
                    }

                    funcContext.runWithExpectedReturnType(expectedType!!, ctx) {
                        case.expr().accept(this)
                    }
                }
            }
        }

        if (!isExhaustiveMatchPattern(matchCaseType, ctx.cases.map { it.pattern() })) {
            throw ExitException(NonExhaustiveMatchPatternsError(matchCaseType, ctx))
        }

        return casesExprTypes.first()
    }

    private fun isExhaustiveMatchPattern(expectedType: Type, patterns: List<stellaParser.PatternContext>): Boolean {
        if (patterns.any { it is stellaParser.PatternVarContext }) return true

        return when (expectedType) {
            BoolType ->
                patterns.any { it is stellaParser.PatternTrueContext } &&
                patterns.any { it is stellaParser.PatternFalseContext }
            UnitType ->
                patterns.any { it is stellaParser.PatternUnitContext }
            NatType -> {
                val numbers = patterns
                    .filterIsInstance<stellaParser.PatternIntContext>()
                    .map { it.n.text.toInt() }
                    .toMutableSet()

                var minSuccLength : Int? = null
                patterns
                    .filterIsInstance<stellaParser.PatternSuccContext>()
                    .map {
                        var result = 0
                        var pattern: stellaParser.PatternContext = it
                        while (pattern is stellaParser.PatternSuccContext) {
                            result++
                            pattern = pattern.pattern()
                        }
                        Pair(result, pattern)
                    }
                    .sortedBy { it.first }
                    .forEach {
                        if (it.second is stellaParser.PatternVarContext && minSuccLength == null) {
                            minSuccLength = it.first
                        }
                        else if (it.second is stellaParser.PatternIntContext){
                            numbers.add(it.first + (it.second as stellaParser.PatternIntContext).n.text.toInt())
                        }
                    }

                if (minSuccLength == null) return false

                return (0..< minSuccLength!!).all { numbers.contains(it) }
            }
            is SumType ->
                patterns.any { it is stellaParser.PatternInrContext } &&
                patterns.any { it is stellaParser.PatternInlContext } &&
                isExhaustiveMatchPattern(expectedType.inl, patterns.filterIsInstance<stellaParser.PatternInlContext>().map { it.pattern() }) &&
                isExhaustiveMatchPattern(expectedType.inr, patterns.filterIsInstance<stellaParser.PatternInrContext>().map { it.pattern() })
            is VariantType -> {
                val variantPatterns = patterns.filterIsInstance<stellaParser.PatternVariantContext>()

                expectedType.variants.all { (name, type) ->
                    variantPatterns.any { it.label.text == name } && (
                        (type == null) ||
                        isExhaustiveMatchPattern(
                            type,
                            variantPatterns
                                .filter { it.label.text == name }
                                .map { it.pattern() })
                    )
                }
            }
            is TupleType ->
                expectedType.types.withIndex().all { (idx, type) ->
                    isExhaustiveMatchPattern(
                        type,
                        patterns
                            .filterIsInstance<stellaParser.PatternTupleContext>()
                            .filter { it.patterns.size == expectedType.types.size }
                            .map { p -> p.patterns[idx] }
                    )
                }
            is RecordType ->
                expectedType.fields.all { field ->
                    isExhaustiveMatchPattern(
                        field.value,
                        patterns
                            .filterIsInstance<stellaParser.PatternRecordContext>()
                            .filter {
                                it.patterns.size == expectedType.fields.size &&
                                        it.patterns.any { lp -> lp.label.text == field.key }
                            }
                            .map { it.patterns.first { lp -> lp.label.text == field.key }.pattern() })
                }
            is ListType -> {
                val listPatterns = patterns
                    .filterIsInstance<stellaParser.PatternListContext>()
                    .map { it.patterns }
                    .toMutableList()
                val consWithLastVariable = mutableListOf<MutableList<stellaParser.PatternContext>>()

                patterns
                    .filterIsInstance<stellaParser.PatternConsContext>()
                    .forEach {
                        val nestedPatterns = mutableListOf<stellaParser.PatternContext>()
                        var currentPattern : stellaParser.PatternContext = it

                        while (currentPattern is stellaParser.PatternConsContext) {
                            nestedPatterns.add(currentPattern.head)
                            currentPattern = currentPattern.tail
                        }

                        if (currentPattern is stellaParser.PatternVarContext) {
                            consWithLastVariable.add(nestedPatterns)
                            listPatterns.add(nestedPatterns)
                        }
                        else if (currentPattern is stellaParser.PatternListContext) {
                            nestedPatterns.addAll(currentPattern.patterns)
                            listPatterns.add(nestedPatterns)
                        }
                    }

                if (consWithLastVariable.isEmpty()) return false

                return consWithLastVariable.any { consPatterns ->
                    for (i in 0..consPatterns.size) {
                        val patternsWithSize = listPatterns.filter { it.size == i }
                        for (j in 0..<i) {
                            if (!isExhaustiveMatchPattern(expectedType.type, patternsWithSize.map { it[j] })) {
                                return false
                            }
                        }
                    }

                    true
                }
            }
            else -> false
        }
    }

    override fun visitLogicNot(ctx: stellaParser.LogicNotContext): Type {
        return funcContext.runWithExpectedReturnType(BoolType, ctx) { ctx.expr().accept(this) }
    }

    override fun visitParenthesisedExpr(ctx: stellaParser.ParenthesisedExprContext): Type {
        return ctx.expr().accept(this)
    }

    override fun visitTail(ctx: stellaParser.TailContext): Type {
        return funcContext.runWithoutExpectations {
            ctx.list.accept(this).ensureOrError(ListType::class) { NotAListError(it, ctx) }
        }
    }

    override fun visitRecord(ctx: stellaParser.RecordContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()
            ?.ensureOrError(RecordType::class) { UnexpectedRecordError(ctx) }

        val fields = ctx.bindings.associate {
            val rhsExpectedType = expectedType?.fields?.get(it.name.text)

            val rhsType =
                if (rhsExpectedType == null)
                    funcContext.runWithoutExpectations { it.rhs.accept(this) }
                else
                    funcContext.runWithExpectedReturnType(rhsExpectedType, ctx) { it.rhs.accept(this) }

            Pair(it.name.text, rhsType)
        }
        val recordType = RecordType(fields)

        if (fields.any { expectedType?.fields?.containsKey(it.key) == false }) {
            throw ExitException(UnexpectedRecordFieldsError(recordType, expectedType!!, ctx))
        }

        if (expectedType != null &&
            expectedType.fields.any { !fields.containsKey(it.key) }) {
            throw ExitException(MissingRecordFieldsError(recordType, expectedType, ctx))
        }

        return recordType
    }

    override fun visitLogicAnd(ctx: stellaParser.LogicAndContext): Type {
        funcContext.runWithExpectedReturnType(BoolType, ctx) { ctx.left.accept(this) }
        funcContext.runWithExpectedReturnType(BoolType, ctx) { ctx.right.accept(this) }

        return BoolType
    }

    override fun visitTypeApplication(ctx: stellaParser.TypeApplicationContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitLetRec(ctx: stellaParser.LetRecContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitLogicOr(ctx: stellaParser.LogicOrContext): Type {
        funcContext.runWithExpectedReturnType(BoolType, ctx) { ctx.left.accept(this) }
        funcContext.runWithExpectedReturnType(BoolType, ctx) { ctx.right.accept(this) }

        return BoolType
    }

    override fun visitTryWith(ctx: stellaParser.TryWithContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPred(ctx: stellaParser.PredContext): Type {
        return funcContext.runWithExpectedReturnType(NatType, ctx) { ctx.n.accept(this) }
    }

    override fun visitTypeAsc(ctx: stellaParser.TypeAscContext): Type {
        val expectedType = ctx.stellatype().accept(this)
        return funcContext.runWithExpectedReturnType(expectedType, ctx) { ctx.expr().accept(this) }
    }

    override fun visitNatRec(ctx: stellaParser.NatRecContext): Type {
        funcContext.runWithExpectedReturnType(NatType, ctx) { ctx.n.accept(this) }

        val zType = funcContext.runWithoutExpectations { ctx.initial.accept(this) }

        funcContext.runWithExpectedReturnType(FuncType(listOf(NatType), FuncType(listOf(zType), zType)), ctx) {
            ctx.step.accept(this)
        }

        return zType
    }

    override fun visitUnfold(ctx: stellaParser.UnfoldContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitRef(ctx: stellaParser.RefContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitDotTuple(ctx: stellaParser.DotTupleContext): Type {
        val exprType = funcContext.runWithoutExpectations { ctx.expr().accept(this) }
        val tupleType = exprType.ensureOrError(TupleType::class) { NotATupleError(it, ctx) }

        val idx = ctx.index.text.toInt()
        if (tupleType.types.size < idx) throw ExitException(TupleIndexOutOfBoundsError(idx, ctx))

        return tupleType.types[idx - 1]
    }

    override fun visitFix(ctx: stellaParser.FixContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()
        val expressionType =
            if (expectedType != null)
                funcContext.runWithExpectedReturnType(FuncType(listOf(expectedType), expectedType), ctx) {
                    ctx.expr().accept(this).ensureOrError(FuncType::class) { NotAFunctionError(it, ctx.expr()) }
                }
            else
                funcContext.runWithoutExpectations { ctx.expr().accept(this) }
        val funcType = expressionType.ensureOrError(FuncType::class) { NotAFunctionError(expressionType, ctx.expr()) }

        return funcType.argTypes.first()
    }

    override fun visitLet(ctx: stellaParser.LetContext): Type {
        val patterBindings = ctx.patternBindings.map {
            val exprType = funcContext.runWithoutExpectations { it.expr().accept(this) }

            when (it.pattern()) {
                is stellaParser.PatternVarContext ->
                    Pair((it.pattern() as stellaParser.PatternVarContext).name.text, exprType)
                else ->
                    TODO("Not yet implemented")
            }
        }

        return funcContext.runWithVariables(patterBindings) { ctx.expr().accept(this) }
    }

    override fun visitAssign(ctx: stellaParser.AssignContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTuple(ctx: stellaParser.TupleContext): Type {
        val tupleType = funcContext
            .getCurrentExpectedReturnType()
            ?.ensureOrError(TupleType::class) { UnexpectedTupleError(it, ctx) }

        if (tupleType != null &&
            tupleType.types.size != ctx.exprs.size) {
            throw ExitException(UnexpectedTupleLengthError(tupleType, ctx))
        }

        return TupleType(ctx.exprs.map { it.accept(this) })
    }

    override fun visitConsList(ctx: stellaParser.ConsListContext): Type {
        val listType = funcContext
            .getCurrentExpectedReturnType()
            ?.ensureOrError(ListType::class) { UnexpectedListError(ctx) }

        val headType =
            if (listType != null)
                funcContext.runWithExpectedReturnType(listType.type, ctx) { ctx.head.accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.head.accept(this) }

        funcContext.runWithExpectedReturnType(headType, ctx) { ctx.tail.accept(this) }

        return ListType(headType)
    }

    override fun visitPatternBinding(ctx: stellaParser.PatternBindingContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitBinding(ctx: stellaParser.BindingContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitMatchCase(ctx: stellaParser.MatchCaseContext): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternVariant(ctx: stellaParser.PatternVariantContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()!!
            .ensureOrError(VariantType::class) { UnexpectedPatternForTypeError(it, ctx) }

        if (!expectedType.variants.containsKey(ctx.label.text)) {
            throw ExitException(UnexpectedPatternForTypeError(expectedType, ctx))
        }

        val expectedVarType = expectedType.variants[ctx.label.text]
        if (expectedVarType != null && ctx.pattern() == null) {
            throw ExitException(UnexpectedNullaryVariantPatternError(expectedType, ctx))
        }
        if (expectedVarType == null && ctx.pattern() != null) {
            throw ExitException(UnexpectedNonNullaryVariantPatternError(expectedType, ctx))
        }

        if (expectedVarType != null) {
            funcContext.runWithExpectedReturnType(expectedVarType, ctx) { ctx.pattern().accept(this) }
        }
        else {
            funcContext.runWithoutExpectations { ctx.pattern().accept(this) }
        }

        return expectedType
    }

    override fun visitPatternInl(ctx: stellaParser.PatternInlContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()!!
            .ensureOrError(SumType::class) { UnexpectedPatternForTypeError(it, ctx) }

        funcContext.runWithExpectedReturnType(expectedType.inl, ctx) { ctx.pattern().accept(this) }

        return expectedType
    }

    override fun visitPatternInr(ctx: stellaParser.PatternInrContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()!!
            .ensureOrError(SumType::class) { UnexpectedPatternForTypeError(it, ctx) }

        funcContext.runWithExpectedReturnType(expectedType.inr, ctx) { ctx.pattern().accept(this) }

        return expectedType
    }

    override fun visitPatternTuple(ctx: stellaParser.PatternTupleContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()!!
            .ensureOrError(TupleType::class) { UnexpectedPatternForTypeError(it, ctx) }

        if (expectedType.types.size != ctx.pattern().size) {
            throw ExitException(UnexpectedPatternForTypeError(expectedType, ctx))
        }

        ctx.pattern().forEachIndexed { i, _ ->
            funcContext.runWithExpectedReturnType(expectedType.types[i], ctx) {
                ctx.pattern()[i].accept(this)
            }
        }

        return expectedType
    }

    override fun visitPatternRecord(ctx: stellaParser.PatternRecordContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()!!
            .ensureOrError(RecordType::class) { UnexpectedPatternForTypeError(it, ctx) }

        if (ctx.patterns.size != expectedType.fields.size ||
            ctx.patterns.any {
                !expectedType.fields.containsKey(it.label.text) ||
                funcContext.runWithPatternReturnType(expectedType.fields[it.label.text]!!) {
                    it.accept(this)
                } != expectedType.fields[it.label.text]
            }) {
            throw ExitException(UnexpectedPatternForTypeError(expectedType, ctx))
        }

        return expectedType
    }

    override fun visitPatternList(ctx: stellaParser.PatternListContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()!!
            .ensureOrError(ListType::class) { UnexpectedPatternForTypeError(it, ctx) }

        ctx.pattern().forEach {
            funcContext.runWithExpectedReturnType(expectedType.type, ctx) {
                it.accept(this)
            }
        }

        return expectedType
    }

    override fun visitPatternCons(ctx: stellaParser.PatternConsContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()!!
            .ensureOrError(ListType::class) { UnexpectedPatternForTypeError(it, ctx) }

        funcContext.runWithExpectedReturnType(expectedType.type, ctx) { ctx.head.accept(this) }
        funcContext.runWithExpectedReturnType(expectedType, ctx) { ctx.tail.accept(this) }

        return expectedType
    }

    override fun visitPatternFalse(ctx: stellaParser.PatternFalseContext?): Type {
        return BoolType
    }

    override fun visitPatternTrue(ctx: stellaParser.PatternTrueContext): Type {
        return BoolType
    }

    override fun visitPatternUnit(ctx: stellaParser.PatternUnitContext): Type {
        return UnitType
    }

    override fun visitPatternInt(ctx: stellaParser.PatternIntContext?): Type {
        return NatType
    }

    override fun visitPatternSucc(ctx: stellaParser.PatternSuccContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType()!!
            .ensureOrError(NatType::class) { UnexpectedPatternForTypeError(it, ctx) }

        funcContext.runWithExpectedReturnType(expectedType, ctx) { ctx.pattern().accept(this) }

        return expectedType
    }

    override fun visitPatternVar(ctx: stellaParser.PatternVarContext): Type {
        val varType = funcContext.getCurrentExpectedReturnType()!!
        funcContext.addVariable(ctx.name.text, varType)

        return varType
    }

    override fun visitParenthesisedPattern(ctx: stellaParser.ParenthesisedPatternContext): Type {
        return ctx.pattern().accept(this)
    }

    override fun visitLabelledPattern(ctx: stellaParser.LabelledPatternContext): Type {
        val recordFieldType = funcContext.getCurrentExpectedReturnType()!!

        funcContext.runWithExpectedReturnType(recordFieldType, ctx) { ctx.pattern().accept(this) }

        return recordFieldType
    }

    override fun visitTypeTuple(ctx: stellaParser.TypeTupleContext): Type {
        return TupleType(ctx.types.map { it.accept(this) })
    }

    override fun visitTypeTop(ctx: stellaParser.TypeTopContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeBool(ctx: stellaParser.TypeBoolContext?): Type {
        return BoolType
    }

    override fun visitTypeRef(ctx: stellaParser.TypeRefContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeRec(ctx: stellaParser.TypeRecContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeSum(ctx: stellaParser.TypeSumContext): Type {
        return SumType(ctx.left.accept(this), ctx.right.accept(this))
    }

    override fun visitTypeVar(ctx: stellaParser.TypeVarContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeVariant(ctx: stellaParser.TypeVariantContext): Type {
        return VariantType(ctx.fieldTypes.associate { Pair(it.label.text, it.stellatype()?.accept(this))  })
    }

    override fun visitTypeUnit(ctx: stellaParser.TypeUnitContext?): Type {
        return UnitType
    }

    override fun visitTypeNat(ctx: stellaParser.TypeNatContext?): Type {
        return NatType
    }

    override fun visitTypeBottom(ctx: stellaParser.TypeBottomContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeParens(ctx: stellaParser.TypeParensContext): Type {
        return ctx.stellatype().accept(this)
    }

    override fun visitTypeFun(ctx: stellaParser.TypeFunContext): Type {
        val paramTypes = ctx.paramTypes.map { it.accept(this) }
        val returnType = ctx.returnType.accept(this)

        return FuncType(paramTypes, returnType)
    }

    override fun visitTypeForAll(ctx: stellaParser.TypeForAllContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeRecord(ctx: stellaParser.TypeRecordContext): Type {
        val fields = ctx.fieldTypes.associate { Pair(it.label.text, it.accept(this)) }

        return RecordType(fields)
    }

    override fun visitTypeList(ctx: stellaParser.TypeListContext): Type {
        return ListType(ctx.stellatype().accept(this))
    }

    override fun visitRecordFieldType(ctx: stellaParser.RecordFieldTypeContext): Type {
        return ctx.stellatype().accept(this)
    }

    override fun visitVariantFieldType(ctx: stellaParser.VariantFieldTypeContext): Type {
        return ctx.stellatype().accept(this)
    }
}