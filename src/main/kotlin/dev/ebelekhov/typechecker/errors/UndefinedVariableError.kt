package dev.ebelekhov.typechecker.errors

import org.antlr.v4.runtime.RuleContext

data class UndefinedVariableError(val varName: String, val parentExpression: RuleContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNDEFINED_VARIABLE:
              в выражении
                ${parentExpression.toStringTree()}
              содержится необъявленная переменная
                $varName
        """.trimIndent()
    }
}