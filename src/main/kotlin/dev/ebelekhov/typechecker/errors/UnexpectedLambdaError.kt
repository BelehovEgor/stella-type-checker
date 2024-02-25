package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class UnexpectedLambdaError(private val expected: Type, private val ctx: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_LAMBDA
                для выражения
                    ${ctx.text}
                ожидается не функциональный тип
                но получен
                    $expected
        """.trimIndent()
    }

}

