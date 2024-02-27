package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class NotAListError(private val type: Type, private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_NOT_A_LIST
                для выражения
                    ${expression.text}
                ожидается лист
                но получен тип
                    $type
        """.trimIndent()
    }
}
