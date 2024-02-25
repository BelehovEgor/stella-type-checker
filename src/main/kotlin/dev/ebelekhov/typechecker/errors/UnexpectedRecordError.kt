package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class UnexpectedRecordError(private val ctx: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_RECORD
                для выражения
                    ${ctx.text}
                ожидается не запись
        """.trimIndent()
    }

}

