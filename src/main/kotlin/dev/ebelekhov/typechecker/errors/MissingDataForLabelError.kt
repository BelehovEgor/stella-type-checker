package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

data class MissingDataForLabelError(val expected: Type, val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
           ERROR_MISSING_DATA_FOR_LABEL:
             выражение
                ${ctx.toStringTree(parser)}
             не содержит даннные для метки, хотя ожидается тег с данными
                $expected
        """.trimIndent()
    }
}