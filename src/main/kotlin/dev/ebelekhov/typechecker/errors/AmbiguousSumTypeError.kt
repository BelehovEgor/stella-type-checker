package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class AmbiguousSumTypeError(private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_AMBIGUOUS_SUM_TYPE:
                для выражения
                    ${ctx.toStringTree(parser)}
                невозможно определить ожидаемый тип-суммы
       """.trimIndent()
    }
}

