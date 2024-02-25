package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.antlr.parser.stellaParser.PatternVarContext
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

        for (decl in ctx.decls) {
            val type = decl.accept(this)
            if (decl is stellaParser.DeclFunContext && decl.name.text == "main") {
                programType = type
            }
        }

        if (programType != null) return programType

        throw ExitException(MissingMainError())
    }

    override fun visitLanguageCore(ctx: stellaParser.LanguageCoreContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitAnExtension(ctx: stellaParser.AnExtensionContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitDeclFun(ctx: stellaParser.DeclFunContext): Type {
        val param = ctx.paramDecl
        val paramType = param.paramType.accept(this)
        val returnType = ctx.returnType.accept(this)

        funcContext.runWithVariable(
            param.name.text,
            paramType) {
            funcContext.runWithExpectedReturnType(
                returnType,
                ctx.returnExpr) {
                ctx.returnExpr.accept(this)
            }
        }

        funcContext.addVariable(ctx.name.text, FuncType(paramType, returnType))

        return FuncType(paramType, returnType)
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
        val argType = ctx.n.accept(this)
        argType.ensure(NatType, ctx)

        return BoolType
    }

    override fun visitVar(ctx: stellaParser.VarContext): Type {
        val name = ctx.name.text
        val varType = funcContext.getVariableType(name)

        if (varType != null) return varType

        throw ExitException(UndefinedVariableError(name, ctx.parent))
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
        val exprType = ctx.expr_.accept(this)
        val label = ctx.label.text

        if (exprType !is RecordType) throw ExitException(NotARecordError(exprType, ctx))

        if (!exprType.fields.containsKey(label)) throw ExitException(UnexpectedFieldAccessError(label, ctx))

        return exprType.fields[label]!!
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

    override fun visitList(ctx: stellaParser.ListContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTryCatch(ctx: stellaParser.TryCatchContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitHead(ctx: stellaParser.HeadContext?): Type {
        TODO("Not yet implemented")
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
        val param = ctx.paramDecl
        val paramType = param.stellatype().accept(this)

        return funcContext.runWithVariable(param.name.text, paramType) {
            FuncType(paramType, ctx.returnExpr.accept(this))
        }
    }

    override fun visitConstInt(ctx: stellaParser.ConstIntContext): Type {
        return NatType
    }

    override fun visitVariant(ctx: stellaParser.VariantContext?): Type {
        TODO("Not yet implemented")
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
        val condType = ctx.condition.accept(this)
        condType.ensure(BoolType, ctx)

        val thenType = ctx.thenExpr.accept(this)
        val elseType = ctx.elseExpr.accept(this)
        thenType.ensure(elseType, ctx)

        return thenType
    }

    override fun visitApplication(ctx: stellaParser.ApplicationContext): Type {
        val f = ctx.`fun`
        val fType = f.accept(this)
        if (fType !is FuncType) throw ExitException(NotAFunctionError(fType, f))

        val expr = ctx.expr
        funcContext.runWithExpectedReturnType(fType.argType, ctx) {
            expr.accept(this)
        }

        return fType.returnType
    }

    override fun visitDeref(ctx: stellaParser.DerefContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitIsEmpty(ctx: stellaParser.IsEmptyContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPanic(ctx: stellaParser.PanicContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitLessThanOrEqual(ctx: stellaParser.LessThanOrEqualContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitSucc(ctx: stellaParser.SuccContext): Type {
        val n = ctx.n
        val nType = n.accept(this)
        nType.ensure(NatType, ctx)

        return NatType
    }

    override fun visitInl(ctx: stellaParser.InlContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitGreaterThanOrEqual(ctx: stellaParser.GreaterThanOrEqualContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitInr(ctx: stellaParser.InrContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitMatch(ctx: stellaParser.MatchContext): Type {
        val matchCaseType = ctx.matchCase.accept(this)
        val cases = ctx.cases
        if (cases.isEmpty()) {
            throw ExitException(IllegalEmptyMatchingError(ctx))
        }
        var casesType = ctx.cases.map { it.accept(this) }


        return NatType
    }

    override fun visitLogicNot(ctx: stellaParser.LogicNotContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitParenthesisedExpr(ctx: stellaParser.ParenthesisedExprContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTail(ctx: stellaParser.TailContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitRecord(ctx: stellaParser.RecordContext): Type {
        val fields = ctx.bindings.associate { Pair(it.name.text, it.rhs.accept(this)) }

        return RecordType(fields)
    }

    override fun visitLogicAnd(ctx: stellaParser.LogicAndContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeApplication(ctx: stellaParser.TypeApplicationContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitLetRec(ctx: stellaParser.LetRecContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitLogicOr(ctx: stellaParser.LogicOrContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTryWith(ctx: stellaParser.TryWithContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPred(ctx: stellaParser.PredContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeAsc(ctx: stellaParser.TypeAscContext): Type {
        return ctx.accept(this)
    }

    override fun visitNatRec(ctx: stellaParser.NatRecContext): Type {
        val until = ctx.n
        until.accept(this).ensure(NatType, ctx)

        val z = ctx.initial
        val zType = z.accept(this)

        val s = ctx.step
        val sType = s.accept(this)
        sType.ensure(FuncType(NatType, FuncType(zType, zType)), ctx)

        return zType
    }

    override fun visitUnfold(ctx: stellaParser.UnfoldContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitRef(ctx: stellaParser.RefContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitDotTuple(ctx: stellaParser.DotTupleContext): Type {
        val exprType = ctx.expr().accept(this)
        if (exprType !is TupleType) throw ExitException(NotATupleError(exprType, ctx))

        val idx = ctx.index.text.toInt()
        if (exprType.types.size < idx) throw ExitException(TupleIndexOutOfBoundsError(idx, ctx))

        return exprType.types[idx - 1]
    }

    override fun visitFix(ctx: stellaParser.FixContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitLet(ctx: stellaParser.LetContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitAssign(ctx: stellaParser.AssignContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTuple(ctx: stellaParser.TupleContext): Type {
        return TupleType(ctx.exprs.map { it.accept(this) })
    }

    override fun visitConsList(ctx: stellaParser.ConsListContext?): Type {
        TODO("Not yet implemented")
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

    override fun visitPatternVariant(ctx: stellaParser.PatternVariantContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternInl(ctx: stellaParser.PatternInlContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternInr(ctx: stellaParser.PatternInrContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternTuple(ctx: stellaParser.PatternTupleContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternRecord(ctx: stellaParser.PatternRecordContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternList(ctx: stellaParser.PatternListContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternCons(ctx: stellaParser.PatternConsContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternFalse(ctx: stellaParser.PatternFalseContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternTrue(ctx: stellaParser.PatternTrueContext): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternUnit(ctx: stellaParser.PatternUnitContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternInt(ctx: stellaParser.PatternIntContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternSucc(ctx: stellaParser.PatternSuccContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitPatternVar(ctx: stellaParser.PatternVarContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitParenthesisedPattern(ctx: stellaParser.ParenthesisedPatternContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitLabelledPattern(ctx: stellaParser.LabelledPatternContext?): Type {
        TODO("Not yet implemented")
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

    override fun visitTypeVariant(ctx: stellaParser.TypeVariantContext?): Type {
        TODO("Not yet implemented")
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
        return ctx.type_.accept(this)
    }

    override fun visitTypeFun(ctx: stellaParser.TypeFunContext): Type {
        val paramType = ctx.paramTypes.first().accept(this)
        val returnType = ctx.returnType.accept(this)

        return FuncType(paramType, returnType)
    }

    override fun visitTypeForAll(ctx: stellaParser.TypeForAllContext?): Type {
        TODO("Not yet implemented")
    }

    override fun visitTypeRecord(ctx: stellaParser.TypeRecordContext): Type {
        val fields = ctx.fieldTypes.associate { Pair(it.label.text, it.accept(this)) }

        return RecordType(fields)
    }

    override fun visitTypeList(ctx: stellaParser.TypeListContext): Type {
        return ctx.type_.accept(this)
    }

    override fun visitRecordFieldType(ctx: stellaParser.RecordFieldTypeContext): Type {
        return ctx.type_.accept(this)
    }

    override fun visitVariantFieldType(ctx: stellaParser.VariantFieldTypeContext): Type {
        return ctx.type_.accept(this)
    }
}