package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class MissingRecordFieldsError(
    private val actual: Type,
    private val expected: Type,
    private val ctx: stellaParser.ExprContext
) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_MISSING_RECORD_FIELDS
                для выражения
                    ${ctx.text}
                ожидается тип
                    $expected
                но передан
                    $actual
        """.trimIndent()
    }

}

