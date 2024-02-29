package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class UnexpectedListError(private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_LIST
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается не список
        """.trimIndent()
    }

}

