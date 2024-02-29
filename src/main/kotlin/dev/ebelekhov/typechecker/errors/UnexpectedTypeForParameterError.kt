package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class UnexpectedTypeForParameterError(
    private val actual: Type,
    private val expected: Type,
    private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_TYPE_FOR_PARAMETER
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается тип
                    $expected
                но получен
                    $actual
        """.trimIndent()
    }

}

