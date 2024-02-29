package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

data class UnexpectedInjectionError(val expected: Type, val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_INJECTION:
                для выражения 
                    ${expression.toStringTree(parser)}
                ожидается
                    $expected
                но передан тип-сумма
       """.trimIndent()
    }
}