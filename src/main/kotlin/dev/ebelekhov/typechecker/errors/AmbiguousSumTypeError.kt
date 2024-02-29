package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class AmbiguousSumTypeError(private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_AMBIGUOUS_SUM_TYPE:
                для выражения
                    ${expression.toStringTree(parser)}
                невозможно определить ожидаемый тип-суммы
       """.trimIndent()
    }
}

