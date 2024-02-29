package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class UnexpectedLambdaError(private val actual: Type, private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_LAMBDA
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается не функциональный тип
                но получен
                    $actual
        """.trimIndent()
    }

}

