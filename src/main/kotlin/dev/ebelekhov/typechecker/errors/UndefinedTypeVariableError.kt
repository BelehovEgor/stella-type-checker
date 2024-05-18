package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.VarType
import org.antlr.v4.runtime.RuleContext

class UndefinedTypeVariableError(private val undefinedVariableType: VarType, private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNDEFINED_TYPE_VARIABLE
                в выражении
                    ${ctx.toStringTree(parser)}
                необъявленный тип
                    $undefinedVariableType
        """.trimIndent()
    }
}