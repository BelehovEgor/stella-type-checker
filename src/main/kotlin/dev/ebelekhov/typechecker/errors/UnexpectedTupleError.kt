package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class UnexpectedTupleError(private val actual: Type, private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_TUPLE
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается не кортеж
                но получен
                    $actual
        """.trimIndent()
    }

}

