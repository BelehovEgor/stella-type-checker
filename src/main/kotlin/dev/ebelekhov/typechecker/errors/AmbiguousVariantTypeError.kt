package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class AmbiguousVariantTypeError(private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_AMBIGUOUS_VARIANT_TYPE:
                для выражения
                    ${expression.toStringTree(parser)}
                невозможно определить вариантный тип
       """.trimIndent()
    }
}