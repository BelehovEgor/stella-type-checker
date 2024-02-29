package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.TupleType

class UnexpectedTupleLengthError(private val expected: TupleType, private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_TUPLE_LENGTH
                для выражения
                    ${expression.toStringTree(parser)}
                ожидается кортеж
                    $expected
                с длинной
                    ${expected.types.size}
        """.trimIndent()
    }

}

