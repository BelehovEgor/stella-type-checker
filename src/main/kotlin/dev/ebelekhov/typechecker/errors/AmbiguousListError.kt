package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class AmbiguousListError(private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_AMBIGUOUS_LIST:
                тип списка
                    ${expression.text}
                невозможно определить 
                в данном контексте отсутсвует ожидаемый тип списка
       """.trimIndent()
    }

}
