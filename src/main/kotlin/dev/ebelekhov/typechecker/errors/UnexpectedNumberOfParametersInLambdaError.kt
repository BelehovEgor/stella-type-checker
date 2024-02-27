package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class UnexpectedNumberOfParametersInLambdaError(val expected: Int, val expression: stellaParser.ExprContext) :
    BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_NUMBER_OF_PARAMETERS_IN_LAMBDA:
                количество параметров анонимной функции
                    ${expression.text}
                не совпадает с ожидаемым количеством параметров 
                    $expected
        """.trimIndent()
    }
}