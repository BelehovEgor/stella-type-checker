package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class NotAReferenceError (private val actual: Type, private val ctx: RuleContext)
    : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_NOT_A_REFERENCE:
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается ссылка
                но получен тип
                    $actual
       """.trimIndent()
    }
}