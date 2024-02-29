package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class NotAListError(private val actual: Type, private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_NOT_A_LIST
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается лист
                но получен тип
                    $actual
        """.trimIndent()
    }
}

