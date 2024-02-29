package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.TupleType
import org.antlr.v4.runtime.RuleContext

class UnexpectedTupleLengthError(private val expected: TupleType, private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_TUPLE_LENGTH
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается кортеж
                    $expected
                с длинной
                    ${expected.types.size}
        """.trimIndent()
    }

}

