package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class AmbiguousVariantTypeError(private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_AMBIGUOUS_VARIANT_TYPE:
                вариантный тип
                    ${expression.text}
                невозможно определить 
                в данном контексте отсутсвует ожидаемый вариантный тип
       """.trimIndent()
    }
}