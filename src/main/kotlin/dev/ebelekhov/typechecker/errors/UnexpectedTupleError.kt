package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class UnexpectedTupleError(private val expected: Type, private val ctx: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_TUPLE
                для выражения
                    ${ctx.text}
                ожидается не кортеж
                но получен
                    $expected
        """.trimIndent()
    }

}

