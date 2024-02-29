package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class UnexpectedFieldAccessError(private val label: String, private val ctx: RuleContext)
    : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_FIELD_ACCESS
                в выражении
                    ${ctx.toStringTree(parser)}
                неожидаемое обращение к полю
                    $label
        """.trimIndent()
    }
}

