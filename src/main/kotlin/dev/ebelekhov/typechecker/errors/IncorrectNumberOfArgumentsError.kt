package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class IncorrectNumberOfArgumentsError(
    private val actual: Int, val expected: Int, val ctx: RuleContext)
    : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_INCORRECT_NUMBER_OF_ARGUMENTS:
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается количество аргументов
                    $expected
                но передается
                    $actual
        """.trimIndent()
    }
}