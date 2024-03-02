package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

data class UnexpectedNonNullaryVariantPatternError(val expected: Type, val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_NON_NULLARY_VARIANT_PATTERN:
                паттерн
                    ${ctx.toStringTree(parser)}
                содержит тег с данными, хотя ожидается тег без данными
                    $expected
       """.trimIndent()
    }
}