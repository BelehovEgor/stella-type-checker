package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class UnexpectedFieldAccessError(private val label: String, private val expression: stellaParser.ExprContext)
    : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_FIELD_ACCESS
                для выражения
                    ${expression.text}
                неожидаемое обращение к полю
                    $label
        """.trimIndent()
    }
}

