package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class NotATupleError (private val actual: Type, private val expression: stellaParser.ExprContext)
    : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_NOT_A_TUPLE:
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается кортеж
                но получен тип
                    $actual
       """.trimIndent()
    }
}
