package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

data class UnexpectedTypeForExpressionError(
    val expected: Type, val actual: Type?, val expression: stellaParser.ExprContext)
    : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION:
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается тип
                    $expected
                но получен тип
                    $actual
       """.trimIndent()
    }
}