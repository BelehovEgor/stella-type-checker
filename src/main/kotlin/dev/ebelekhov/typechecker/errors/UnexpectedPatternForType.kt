package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class UnexpectedPatternForTypeError(
    private val actual: Type,
    private val expected: Type,
    private val ctx: stellaParser.PatternContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_PATTERN_FOR_TYPE
                для выражения
                    ${ctx.text}
                ожидается 
                    $expected
                но получен тип
                    $actual
        """.trimIndent()
    }

}
