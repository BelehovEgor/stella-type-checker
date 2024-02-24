package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class TupleIndexOutOfBoundsError(private val idx: Int, private val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_TUPLE_INDEX_OUT_OF_BOUNDS
                в выражении
                    ${expression.text}
                неожидаемый доступ к элементу
                    $idx
        """.trimIndent()
    }

}

