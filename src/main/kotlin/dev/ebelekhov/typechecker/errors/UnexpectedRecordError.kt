package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class UnexpectedRecordError(private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_RECORD
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается не запись
        """.trimIndent()
    }

}

