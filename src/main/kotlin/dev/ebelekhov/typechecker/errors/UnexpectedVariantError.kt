package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class UnexpectedVariantError(
    private val expected: Type,
    private val expression: stellaParser.ExprContext
) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_VARIANT:
                получена вариант 
                    ${expression.toStringTree(parser)}
                но ожидается не вариантный тип
                    $expected
       """.trimIndent()
    }

}