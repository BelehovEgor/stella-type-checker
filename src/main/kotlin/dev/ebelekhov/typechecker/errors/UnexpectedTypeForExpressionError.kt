package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

data class UnexpectedTypeForExpressionError(
    val expected: Type, val actual: Type?, val ctx: RuleContext)
    : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION:
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается тип
                    $expected
                но получен тип
                    $actual
       """.trimIndent()
    }
}