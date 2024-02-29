package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class UnexpectedTupleError(private val actual: Type, private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_TUPLE
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается не кортеж
                но получен
                    $actual
        """.trimIndent()
    }

}

