package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class NonExhaustiveLetPatternsError(val expected: Type, val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
           ERROR_NONEXHAUSTIVE_LET_PATTERNS:
                не все образцы для типа
                    $expected
                перечислены в выражении
                    ${ctx.toStringTree(parser)}
        """.trimIndent()
    }
}