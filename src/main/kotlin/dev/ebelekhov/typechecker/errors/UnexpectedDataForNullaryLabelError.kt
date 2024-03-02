package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

data class UnexpectedDataForNullaryLabelError(val expected: Type, val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
           ERROR_UNEXPECTED_DATA_FOR_NULLARY_LABEL:
             выражение
                ${ctx.toStringTree(parser)}
             содержит даннные для метки, хотя ожидается тег без данных
                $expected
        """.trimIndent()
    }
}