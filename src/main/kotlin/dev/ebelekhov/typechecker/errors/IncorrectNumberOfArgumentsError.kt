package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class IncorrectNumberOfArgumentsError(private val actual: Int, val expected: Int, val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_INCORRECT_NUMBER_OF_ARGUMENTS:
                количество аргументов на вызов
                    ${expression.text}
                ожидается 
                    $expected
                а передается
                    $actual
        """.trimIndent()
    }
}