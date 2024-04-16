package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class AmbiguousReferenceTypeError(private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
           ERROR_AMBIGUOUS_REFERENCE_TYPE:
                неоднозначный тип адреса памяти
                ${ctx.toStringTree(parser)}
       """.trimIndent()
    }
}