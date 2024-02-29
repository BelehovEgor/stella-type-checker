package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class UnexpectedNumberOfParametersInLambdaError(val expected: Int, val expression: stellaParser.ExprContext) :
    BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_NUMBER_OF_PARAMETERS_IN_LAMBDA:
                количество параметров анонимной функции
                    ${expression.toStringTree(parser)}
                не совпадает с ожидаемым количеством параметров 
                    $expected
        """.trimIndent()
    }
}