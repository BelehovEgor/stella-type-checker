package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class UnexpectedRecordFieldsError(
    private val actual: Type,
    private val expected: Type,
    private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_RECORD_FIELDS
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается тип
                    $expected
                но передан
                    $actual
        """.trimIndent()
    }

}

