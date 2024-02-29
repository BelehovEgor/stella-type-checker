package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class IllegalEmptyMatchingError(private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_ILLEGAL_EMPTY_MATCHING
                match выражение
                    ${expression.toStringTree(parser)}
                имеет пустой список выбора
        """.trimIndent()
    }
}

