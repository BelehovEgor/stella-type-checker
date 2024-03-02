package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

data class UnexpectedNullaryVariantPatternError(val expected: Type, val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_NULLARY_VARIANT_PATTERN:
                паттерн
                    ${ctx.toStringTree(parser)}
                содержит тег без данных, хотя ожидается тег с данными
                    $expected
        """.trimIndent()
    }
}

