package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class NotARecordError(private val actual: Type, private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_NOT_A_RECORD
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается запись
                но получен тип
                    $actual
        """.trimIndent()
    }
}

