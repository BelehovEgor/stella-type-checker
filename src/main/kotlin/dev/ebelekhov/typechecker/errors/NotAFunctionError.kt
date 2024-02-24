package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class NotAFunctionError(private val expression: stellaParser.ExprContext, private val type: Type)
    : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
           ERROR_NOT_A_FUNCTION:
             для выражения
               ${expression.toStringTree(parser)}
             ожидается функциональный тип
             но получен тип
               $type
       """.trimIndent()
    }
}