package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class MissingRecordFieldsError(
    private val actual: Type,
    private val expected: Type,
    private val expression: stellaParser.ExprContext
) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_MISSING_RECORD_FIELDS
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается тип
                    $expected
                но передан
                    $actual
        """.trimIndent()
    }

}

