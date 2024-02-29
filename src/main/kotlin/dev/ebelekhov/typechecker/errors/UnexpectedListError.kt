package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class UnexpectedListError(private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_LIST
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается не список
        """.trimIndent()
    }

}

