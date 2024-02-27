package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class UnexpectedPatternForTypeError(
    private val actual: Type,
    private val ctx: stellaParser.PatternContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_PATTERN_FOR_TYPE:
                образец
                    ${ctx.text}
                не соответствует типу разбираемого выражения
                    $actual
        """.trimIndent()
    }

}
