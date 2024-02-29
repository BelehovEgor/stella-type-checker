package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

data class UnexpectedInjectionError(val expected: Type, val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_INJECTION:
                для выражения 
                    ${ctx.toStringTree(parser)}
                ожидается
                    $expected
                но передан тип-сумма
       """.trimIndent()
    }
}