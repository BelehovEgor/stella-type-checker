package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class AmbiguousListError(private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_AMBIGUOUS_LIST:
                для выражения 
                    ${expression.toStringTree(parser)}
                невозможно определить тип списка
       """.trimIndent()
    }

}

