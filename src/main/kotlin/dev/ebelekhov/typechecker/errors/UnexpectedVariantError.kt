package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class UnexpectedVariantError(
    private val expected: Type,
    private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_VARIANT:
                получена вариант 
                    ${ctx.toStringTree(parser)}
                но ожидается не вариантный тип
                    $expected
       """.trimIndent()
    }

}