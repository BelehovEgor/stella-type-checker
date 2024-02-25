package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.TupleType

class UnexpectedTupleLengthError(private val expected: TupleType, private val ctx: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_TUPLE_LENGTH
                для выражения
                    ${ctx.text}
                ожидается кортеж
                    $expected
                с длинной
                    ${expected.types.size}
        """.trimIndent()
    }

}

