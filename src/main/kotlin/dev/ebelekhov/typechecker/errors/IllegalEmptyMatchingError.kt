package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class IllegalEmptyMatchingError(private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_ILLEGAL_EMPTY_MATCHING
                match выражение
                    ${ctx.toStringTree(parser)}
                имеет пустой список выбора
        """.trimIndent()
    }
}

