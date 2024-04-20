package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.antlr.parser.stellaParserVisitor
import dev.ebelekhov.typechecker.errors.*
import dev.ebelekhov.typechecker.types.*
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode

class StellaVisitor(private val funcContext: FuncContext)
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
        ctx.decls.forEach {
            val type = it.accept(this)

            if (it is stellaParser.DeclFunContext) {
                funcContext.addVariable(it.name.text, type)
            }
        }

        val programType = funcContext.getVariableType("main") ?: throw ExitException(MissingMainError())
        val programFuncType = programType.ensureOrError(FuncType::class) { NotAFunctionError(it, ctx) }

        val mainArgsSize = programFuncType.argTypes.size
        if (mainArgsSize != 1) {
            throw ExitException(IncorrectArityOfMainError(mainArgsSize))
        }

        return programFuncType
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

        funcContext.runWithVariable(ctx.name.text, funcType) {
            funcContext.runWithVariables(paramsInfo) {
                funcContext.runWithScope {
                    val nestedFunctions = ctx.localDecls.filterIsInstance<stellaParser.DeclFunContext>().map {
                        val type = it.accept(this)
                        funcContext.addVariable(it.name.text, type)
                        Pair(it.name.text, type)
                    }

                    funcContext.runWithVariables(nestedFunctions){
                        funcContext.runWithExpectedReturnType(returnType, ctx.returnExpr) {
                            ctx.returnExpr.accept(this)
                        }
                    }
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

    override fun visitDeclExceptionType(ctx: stellaParser.DeclExceptionTypeContext): Type {
        val type = ctx.exceptionType.accept(this)

        funcContext.addExceptionExpectedType(type)

        return type
    }

    override fun visitDeclExceptionVariant(ctx: stellaParser.DeclExceptionVariantContext): Type {
        val name = ctx.name.text
        val type = ctx.variantType.accept(this)

        funcContext.addExceptionVariantPair(name, type)

        return type
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
        if (recordType.getTypeByName(label) == null) throw ExitException(UnexpectedFieldAccessError(label, ctx))

        return recordType.getTypeByName(label)!!
    }

    override fun visitGreaterThan(ctx: stellaParser.GreaterThanContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitEqual(ctx: stellaParser.EqualContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitThrow(ctx: stellaParser.ThrowContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()
            ?: throw ExitException(AmbiguousThrowTypeError(ctx))

        val exceptionType = funcContext.getExceptionExpectedType()

        funcContext.runWithExpectedReturnType(exceptionType, ctx) {
            ctx.expr().accept(this)
        }

        return expectedType
    }

    override fun visitMultiply(ctx: stellaParser.MultiplyContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitConstMemory(ctx: stellaParser.ConstMemoryContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType(ReferenceType::class) { UnexpectedMemoryAddressError(ctx, it) }
            ?: throw ExitException(AmbiguousReferenceTypeError(ctx))

        return expectedType
    }

    override fun visitList(ctx: stellaParser.ListContext): Type {
        var listType = funcContext.getCurrentExpectedReturnType(ListType::class) { UnexpectedListError(ctx) }
        if (listType is BotType) listType = ListType(BotType())

        if (ctx.exprs.isEmpty()) { return listType ?: throw ExitException(AmbiguousListTypeError(ctx)) }

        val firstElemType =
            if (listType != null && listType is ListType)
                funcContext.runWithExpectedReturnType(listType.type, ctx) { ctx.exprs.first().accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.exprs.first().accept(this) }

        ctx.exprs.forEach {
            funcContext.runWithExpectedReturnType(firstElemType, ctx) { it.accept(this) }
        }

        return ListType(firstElemType)
    }

    override fun visitTryCatch(ctx: stellaParser.TryCatchContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()
        val exceptionType = funcContext.getExceptionExpectedType()

        val exprType =
            if (expectedType != null)
                funcContext.runWithExpectedReturnType(expectedType, ctx) { ctx.tryExpr.accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.tryExpr.accept(this) }

        funcContext.runWithScope {
            funcContext.runWithExpectedReturnType(exceptionType, ctx) {
                ctx.pat.accept(this).ensureOrError(exceptionType) { UnexpectedPatternForTypeError(it, ctx) }
            }

            funcContext.runWithExpectedReturnType(exprType, ctx) { ctx.fallbackExpr.accept(this) }
        }

        return exprType
    }

    override fun visitTryCastAs(ctx: stellaParser.TryCastAsContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()

        funcContext.runWithoutExpectations { ctx.tryExpr.accept(this) }

        val castType = funcContext.runWithoutExpectations { ctx.type_.accept(this) }

        funcContext.runWithExpectedReturnType(castType, ctx) {
            ctx.pattern_.accept(this).ensureOrError(castType) { UnexpectedPatternForTypeError(it, ctx) }
        }

        val exprType = if (expectedType != null)
            funcContext.runWithExpectedReturnType(expectedType, ctx) { ctx.expr_.accept(this) }
        else
            funcContext.runWithoutExpectations { ctx.expr_.accept(this) }

        funcContext.runWithExpectedReturnType(exprType, ctx) { ctx.fallbackExpr.accept(this) }

        return exprType
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

    override fun visitSequence(ctx: stellaParser.SequenceContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()
        val unitExprs = ctx.expr().subList(0, ctx.expr().size - 1)
        val lastExpr = ctx.expr().last()

        unitExprs.forEach {
            funcContext.runWithExpectedReturnType(UnitType, ctx) {
                it.accept(this)
            }
        }

        return if (expectedType != null)
                funcContext.runWithExpectedReturnType(expectedType, ctx) { lastExpr.accept(this) }
            else
                funcContext.runWithoutExpectations { lastExpr.accept(this) }
    }

    override fun visitConstFalse(ctx: stellaParser.ConstFalseContext?): Type {
        return BoolType
    }

    override fun visitAbstraction(ctx: stellaParser.AbstractionContext): Type {
        val expectedFuncType = funcContext
           .getCurrentExpectedReturnType(FuncType::class) { type -> UnexpectedLambdaError(type, ctx) }

        val params = ctx.paramDecls
        if (expectedFuncType != null &&
            expectedFuncType is FuncType &&
            expectedFuncType.argTypes.size != params.size) {
            throw ExitException(UnexpectedNumberOfParametersInLambdaError(expectedFuncType.argTypes.size, ctx))
        }

        val paramTypes = params.mapIndexed { idx, value ->
            val type =
                if (expectedFuncType == null || expectedFuncType !is FuncType)
                    funcContext.runWithoutExpectations { value.stellatype().accept(this) }
                else
                    funcContext.runWithExpectedReturnType(expectedFuncType.argTypes[idx], ctx) {
                        funcContext.ensureOrErrorWithContext(
                            value.stellatype().accept(this),
                            expectedFuncType.argTypes[idx],
                            ctx) {
                            UnexpectedTypeForParameterError(it, expectedFuncType.argTypes[idx], ctx)
                        }
                    }

            Pair(value.name.text, type)
        }

        return funcContext.runWithVariables(paramTypes) {
            val returnType =
                if (expectedFuncType !is FuncType)
                    funcContext.runWithoutExpectations { ctx.returnExpr.accept(this) }
                else
                    funcContext.runWithExpectedReturnType(expectedFuncType.returnType, ctx) {
                        funcContext.ensureWithContext(
                            ctx.returnExpr.accept(this),
                            expectedFuncType.returnType,
                            ctx)
                    }

            FuncType(paramTypes.map { it.second }, returnType)
        }
    }

    override fun visitConstInt(ctx: stellaParser.ConstIntContext): Type {
        return NatType
    }

    override fun visitVariant(ctx: stellaParser.VariantContext): Type {
        val expectedVariantType = funcContext
            .getCurrentExpectedReturnType(VariantType::class) { UnexpectedVariantError(it, ctx) }
            ?: throw ExitException(AmbiguousVariantTypeError(ctx))

        val variantLabel = ctx.label.text
        if (expectedVariantType is VariantType) {
            val variantField = expectedVariantType.variants.firstOrNull { it.first == variantLabel }
                ?: throw ExitException(UnexpectedVariantLabelError(variantLabel, expectedVariantType, ctx))

            if (variantField.second == null && ctx.rhs != null) {
                throw ExitException(UnexpectedDataForNullaryLabelError(expectedVariantType, ctx))
            }

            if (variantField.second != null && ctx.rhs == null) {
                throw ExitException(MissingDataForLabelError(expectedVariantType, ctx))
            }

            if (variantField.second != null) {
                funcContext.runWithExpectedReturnType(variantField.second!!, ctx) { ctx.rhs.accept(this) }
            }

            return expectedVariantType
        }

        val fieldType = funcContext.runWithoutExpectations { ctx.rhs.accept(this) }
        return VariantType(listOf(Pair(variantLabel, fieldType)))
    }

    override fun visitConstTrue(ctx: stellaParser.ConstTrueContext?): Type {
        return BoolType
    }

    override fun visitSubtract(ctx: stellaParser.SubtractContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeCast(ctx: stellaParser.TypeCastContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()

        funcContext.runWithoutExpectations { ctx.expr().accept(this) }

        val castType =
            if (expectedType != null)
                funcContext.runWithExpectedReturnType(expectedType, ctx) { ctx.type_.accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.type_.accept(this) }

        return castType
    }

    override fun visitIf(ctx: stellaParser.IfContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()

        funcContext.runWithExpectedReturnType(BoolType, ctx) { ctx.condition.accept(this) }

        val thenType =
            if (expectedType != null)
                funcContext.runWithExpectedReturnType(expectedType, ctx) { ctx.thenExpr.accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.thenExpr.accept(this) }

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

    override fun visitDeref(ctx: stellaParser.DerefContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()

        val exprType = if (expectedType != null)
                funcContext.runWithExpectedReturnType(ReferenceType(expectedType), ctx) { ctx.expr().accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.expr().accept(this) }
        val refType = exprType.ensureOrError(ReferenceType::class) { NotAReferenceError(exprType, ctx) }

        if (expectedType != null) refType.innerType.ensure(expectedType, ctx)

        return refType.innerType
    }

    override fun visitIsEmpty(ctx: stellaParser.IsEmptyContext): Type {
        funcContext.runWithoutExpectations {
            ctx.list.accept(this).ensureOrError(ListType::class) { NotAListError(it, ctx) }
        }

        return BoolType
    }

    override fun visitPanic(ctx: stellaParser.PanicContext): Type {
        return funcContext
            .getCurrentExpectedReturnType()
            ?: throw ExitException(AmbiguousPanicTypeError(ctx))
    }

    override fun visitLessThanOrEqual(ctx: stellaParser.LessThanOrEqualContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitSucc(ctx: stellaParser.SuccContext): Type {
        return funcContext.runWithExpectedReturnType(NatType, ctx) { ctx.n.accept(this) }
    }

    override fun visitInl(ctx: stellaParser.InlContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType(SumType::class) { UnexpectedInjectionError(it, ctx) }
            ?: throw ExitException(AmbiguousSumTypeError(ctx))

        if (expectedType is SumType) {
            funcContext.runWithExpectedReturnType(expectedType.inl, ctx) { ctx.expr().accept(this) }

            return expectedType
        }

        return SumType(funcContext.runWithoutExpectations { ctx.expr().accept(this) }, BotType())
    }

    override fun visitGreaterThanOrEqual(ctx: stellaParser.GreaterThanOrEqualContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitInr(ctx: stellaParser.InrContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType(SumType::class) { UnexpectedInjectionError(it, ctx) }
            ?: throw ExitException(AmbiguousSumTypeError(ctx))

        if (expectedType is SumType) {
            funcContext.runWithExpectedReturnType(expectedType.inr, ctx) { ctx.expr().accept(this) }

            return expectedType
        }

        return SumType(BotType(), funcContext.runWithoutExpectations { ctx.expr().accept(this) })
    }

    override fun visitMatch(ctx: stellaParser.MatchContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()

        val matchCaseType = funcContext.runWithoutExpectations { ctx.expr().accept(this) }
        if (ctx.cases.isEmpty()) {
            throw ExitException(IllegalEmptyMatchingError(ctx))
        }

        val casesExprTypes = ctx.cases.map { case ->
            funcContext.runWithScope {
                funcContext.runWithExpectedReturnType(matchCaseType, ctx) {
                    funcContext.ensureOrErrorWithContext(case.pattern().accept(this), matchCaseType, ctx) {
                        UnexpectedPatternForTypeError(matchCaseType, case.pattern())
                    }
                }

                if (expectedType != null)
                    funcContext.runWithExpectedReturnType(expectedType, ctx) { case.expr().accept(this) }
                else
                    funcContext.runWithoutExpectations { case.expr().accept(this) }
            }
        }

        casesExprTypes.forEach {
            if (it != casesExprTypes[0]) {
                throw ExitException(UnexpectedTypeForExpressionError(casesExprTypes[0], it, ctx))
            }
        }

        if (!isExhaustiveMatchPattern(matchCaseType, ctx.cases.map { it.pattern() })) {
            throw ExitException(NonExhaustiveMatchPatternsError(matchCaseType, ctx))
        }

        return casesExprTypes.first()
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
            .getCurrentExpectedReturnType(RecordType::class) { UnexpectedRecordError(ctx) }

        if (expectedType is RecordType) {
            val fields = ctx.bindings.map {
                val rhsExpectedType = expectedType.getTypeByName(it.name.text)

                val rhsType =
                    if (rhsExpectedType == null)
                        funcContext.runWithoutExpectations { it.rhs.accept(this) }
                    else
                        funcContext.runWithExpectedReturnType(rhsExpectedType, ctx) { it.rhs.accept(this) }

                Pair(it.name.text, rhsType)
            }
            val recordType = RecordType(fields)

            if (!funcContext.hasExtension(StellaExtension.StructuralSubtyping)) {
                if (fields.any { expectedType.getTypeByName(it.first) == null }) {
                    throw ExitException(UnexpectedRecordFieldsError(recordType, expectedType, ctx))
                }
            }

            if (expectedType.fields.any { expectedField -> !fields.any {expectedField.first == it.first} }) {
                throw ExitException(MissingRecordFieldsError(recordType, expectedType, ctx))
            }

            return expectedType
        }

        val fields = ctx.bindings.map {
            val rhsType = funcContext.runWithoutExpectations { it.rhs.accept(this) }

            Pair(it.name.text, rhsType)
        }
        val recordType = RecordType(fields)

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

    override fun visitLetRec(ctx: stellaParser.LetRecContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()!!

        return funcContext.runWithScope {
            ctx.patternBindings.map {
                val patternType = funcContext.runWithoutExpectations {
                    it.pattern().accept(this)
                }

                funcContext.runWithExpectedReturnType(patternType, ctx) {
                    it.expr().accept(this)
                }

                if (!isExhaustiveMatchPattern(patternType, ctx.patternBindings.map { it.pattern() })) {
                    throw ExitException(NonExhaustiveLetPatternsError(patternType, ctx))
                }
            }

            funcContext.runWithExpectedReturnType(expectedType, ctx) {
                ctx.expr().accept(this)
            }
        }
    }

    override fun visitLogicOr(ctx: stellaParser.LogicOrContext): Type {
        funcContext.runWithExpectedReturnType(BoolType, ctx) { ctx.left.accept(this) }
        funcContext.runWithExpectedReturnType(BoolType, ctx) { ctx.right.accept(this) }

        return BoolType
    }

    override fun visitTryWith(ctx: stellaParser.TryWithContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()

        val tryType =
            if (expectedType != null)
                funcContext.runWithExpectedReturnType(expectedType, ctx) { ctx.tryExpr.accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.tryExpr.accept(this) }

        funcContext.runWithExpectedReturnType(tryType, ctx) { ctx.fallbackExpr.accept(this) }

        if (expectedType != null) tryType.ensure(expectedType, ctx)

        return tryType
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

    override fun visitRef(ctx: stellaParser.RefContext): Type {
        val expectedType = funcContext
            .getCurrentExpectedReturnType(ReferenceType::class) { UnexpectedReferenceError(ctx) }

        val innerType = if (expectedType != null && expectedType is ReferenceType)
                funcContext.runWithExpectedReturnType(expectedType.innerType, ctx) { ctx.expr().accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.expr().accept(this) }

        return expectedType ?: ReferenceType(innerType)
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
        if (funcType.argTypes.size != 1) {
            throw ExitException(NotAFunctionError(expressionType, ctx.expr()))
        }
        funcType.argTypes[0].ensure(funcType.returnType, ctx)

        return funcType.argTypes.first()
    }

    override fun visitLet(ctx: stellaParser.LetContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()

        return funcContext.runWithScope {
            ctx.patternBindings.map {
                val exprType = funcContext.runWithoutExpectations {
                    it.expr().accept(this)
                }

                val patternType = funcContext.runWithExpectedReturnType(exprType, ctx) {
                    it.pattern().accept(this)
                }

                if (!isExhaustiveMatchPattern(patternType, ctx.patternBindings.map { it.pattern() })) {
                    throw ExitException(NonExhaustiveLetPatternsError(patternType, ctx))
                }
            }

            if (expectedType != null)
                funcContext.runWithExpectedReturnType(expectedType, ctx) { ctx.expr().accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.expr().accept(this) }
        }
    }

    override fun visitAssign(ctx: stellaParser.AssignContext): Type {
        funcContext.getCurrentExpectedReturnType(UnitType::class) { UnexpectedTypeForExpressionError(UnitType, it, ctx) }

        val lhsType = funcContext.runWithoutExpectations { ctx.lhs.accept(this) }
        val referenceType = lhsType.ensureOrError(ReferenceType::class) { NotAReferenceError(it, ctx) }

        funcContext.runWithExpectedReturnType(referenceType.innerType, ctx) { ctx.rhs.accept(this) }

        return UnitType
    }

    override fun visitTuple(ctx: stellaParser.TupleContext): Type {
        val tupleType = funcContext
            .getCurrentExpectedReturnType(TupleType::class) { UnexpectedTupleError(it, ctx) }

        if (tupleType != null && tupleType is TupleType && tupleType.types.size != ctx.exprs.size) {
            throw ExitException(UnexpectedTupleLengthError(tupleType, ctx))
        }

        return TupleType(ctx.exprs.mapIndexed { idx, value ->
            if (tupleType != null && tupleType is TupleType)
                funcContext.runWithExpectedReturnType(tupleType.types[idx], ctx) {
                    value.accept(this)
                }
            else
                funcContext.runWithoutExpectations {
                    value.accept(this)
                }
        })
    }

    override fun visitConsList(ctx: stellaParser.ConsListContext): Type {
        val listType = funcContext
            .getCurrentExpectedReturnType(ListType::class) { UnexpectedListError(ctx) }

        val headType =
            if (listType != null && listType is ListType)
                funcContext.runWithExpectedReturnType(listType.type, ctx) { ctx.head.accept(this) }
            else
                funcContext.runWithoutExpectations { ctx.head.accept(this) }

        val expectedListType = ListType(headType)
        funcContext.runWithExpectedReturnType(expectedListType, ctx) { ctx.tail.accept(this) }

        return expectedListType
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

        val variantField = expectedType.variants.firstOrNull { it.first == ctx.label.text }
            ?: throw ExitException(UnexpectedPatternForTypeError(expectedType, ctx))

        if (variantField.second != null && ctx.pattern() == null) {
            throw ExitException(UnexpectedNullaryVariantPatternError(expectedType, ctx))
        }
        if (variantField.second == null && ctx.pattern() != null) {
            throw ExitException(UnexpectedNonNullaryVariantPatternError(expectedType, ctx))
        }

        if (variantField.second != null) {
            funcContext.runWithExpectedReturnType(variantField.second!!, ctx) { ctx.pattern().accept(this) }
        }
        else if (ctx.pattern() != null) {
            funcContext.runWithoutExpectations { ctx.pattern().accept(this) }
        }

        return expectedType
    }

    override fun visitPatternAsc(ctx: stellaParser.PatternAscContext): Type {
        val valType = ctx.stellatype().accept(this)

        return funcContext.runWithExpectedReturnType(valType, ctx) {
            ctx.pattern().accept(this).ensureOrError(valType) { UnexpectedPatternForTypeError(it, ctx) }
        }
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
                expectedType.getTypeByName(it.label.text) == null ||
                funcContext.runWithPatternReturnType(expectedType.getTypeByName(it.label.text)!!) {
                    it.accept(this)
                } != expectedType.getTypeByName(it.label.text)
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

    override fun visitPatternCastAs(ctx: stellaParser.PatternCastAsContext): Type {
        val expectedType = funcContext.getCurrentExpectedReturnType()

        val castType = funcContext.runWithoutExpectations { ctx.type_.accept(this) }
        val patternType = funcContext.runWithExpectedReturnType(castType, ctx) { ctx.pattern_.accept(this)}

        return expectedType ?: patternType
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
        val varType = funcContext.getCurrentExpectedReturnType()
            ?: throw ExitException(AmbiguousPatternTypeError(ctx))
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
        return TopType
    }

    override fun visitTypeBool(ctx: stellaParser.TypeBoolContext?): Type {
        return BoolType
    }

    override fun visitTypeRef(ctx: stellaParser.TypeRefContext): Type {
        return ReferenceType(ctx.type_.accept(this))
    }

    override fun visitTypeRec(ctx: stellaParser.TypeRecContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeAuto(ctx: stellaParser.TypeAutoContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeSum(ctx: stellaParser.TypeSumContext): Type {
        return SumType(ctx.left.accept(this), ctx.right.accept(this))
    }

    override fun visitTypeVar(ctx: stellaParser.TypeVarContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeVariant(ctx: stellaParser.TypeVariantContext): Type {
        return VariantType(ctx.fieldTypes.map { Pair(it.label.text, it.stellatype()?.accept(this))  })
    }

    override fun visitTypeUnit(ctx: stellaParser.TypeUnitContext?): Type {
        return UnitType
    }

    override fun visitTypeNat(ctx: stellaParser.TypeNatContext?): Type {
        return NatType
    }

    override fun visitTypeBottom(ctx: stellaParser.TypeBottomContext?): Type {
        return BotType()
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
        val fields = ctx.fieldTypes.map { Pair(it.label.text, it.accept(this)) }

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