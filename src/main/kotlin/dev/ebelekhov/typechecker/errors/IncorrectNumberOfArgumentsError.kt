package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class IncorrectNumberOfArgumentsError(
    private val actual: Int, val expected: Int, val expression: stellaParser.ExprContext)
    : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_INCORRECT_NUMBER_OF_ARGUMENTS:
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается количество аргументов
                    $expected
                но передается
                    $actual
        """.trimIndent()
    }
}