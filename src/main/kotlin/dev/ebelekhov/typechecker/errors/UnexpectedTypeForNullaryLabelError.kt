package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class UnexpectedTypeForNullaryLabelError(
    private val expected: Type,
    private val ctx: RuleContext
) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_TYPE_FOR_NULLARY_LABEL
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается тип
                    $expected
                но типа нет
        """.trimIndent()
    }

}
