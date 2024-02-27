package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class UnexpectedVariantError(
    private val expected: Type,
    private val expression: stellaParser.ExprContext
) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_VARIANT:
                получена вариант 
                    ${expression.text}
                но ожидается не вариантный тип
                    $expected
       """.trimIndent()
    }

}