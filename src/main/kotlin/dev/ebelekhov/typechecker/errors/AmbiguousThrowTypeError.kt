package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class AmbiguousThrowTypeError (private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_AMBIGUOUS_THROW_TYPE:
                для выражения 
                    ${ctx.toStringTree(parser)}
                невозможно определить тип throw
       """.trimIndent()
    }

}