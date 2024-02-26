package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class UnexpectedListError(private val ctx: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_LIST
                для выражения
                    ${ctx.text}
                ожидается не список
        """.trimIndent()
    }

}

