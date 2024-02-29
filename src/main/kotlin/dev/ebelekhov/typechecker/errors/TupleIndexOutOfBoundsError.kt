package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class TupleIndexOutOfBoundsError(private val idx: Int, private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_TUPLE_INDEX_OUT_OF_BOUNDS
                в выражении
                    ${ctx.toStringTree(parser)}
                неожидаемый доступ к элементу
                    $idx
        """.trimIndent()
    }

}

