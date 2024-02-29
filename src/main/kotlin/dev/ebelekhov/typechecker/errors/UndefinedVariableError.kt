package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

data class UndefinedVariableError(val varName: String, val expression: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNDEFINED_VARIABLE:
                в выражении
                    ${expression.toStringTree(parser)}
                содержится необъявленная переменная
                    $varName
        """.trimIndent()
    }
}