package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

data class UndefinedVariableError(val varName: String, val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNDEFINED_VARIABLE:
                в выражении
                    ${ctx.toStringTree(parser)}
                содержится необъявленная переменная
                    $varName
        """.trimIndent()
    }
}