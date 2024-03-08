package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class AmbiguousPatternTypeError(private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_AMBIGUOUS_PATTERN_TYPE:
                невозможно определить тип для паттерна
                    ${ctx.toStringTree(parser)}
       """.trimIndent()
    }
}