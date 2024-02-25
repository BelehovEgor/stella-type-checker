package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class UnexpectedTypeForParameterError(
    private val actual: Type,
    private val expected: Type,
    private val ctx: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_TYPE_FOR_PARAMETER
                для выражения
                    ${ctx.text}
                ожидается тип
                    $expected
                но получен
                    $actual
        """.trimIndent()
    }

}

