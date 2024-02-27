package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class AmbiguousSumTypeError(private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_AMBIGUOUS_SUM_TYPE:
                тип инъекции
                    ${expression.text}
                невозможно определить 
                в данном контексте отсутсвует ожидаемый тип-сумма
       """.trimIndent()
    }
}

