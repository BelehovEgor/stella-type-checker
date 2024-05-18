package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class IncorrectNumberOfTypeArgumentsError(
    private val actual: Int, val expected: Int, val ctx: RuleContext)
    : BaseError() {
        override fun getMessage(parser: stellaParser): String {
            return """
            ERROR_INCORRECT_NUMBER_OF_TYPE_ARGUMENTS:
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается количество типов аргументов
                    $expected
                но передается
                    $actual
        """.trimIndent()
        }
    }