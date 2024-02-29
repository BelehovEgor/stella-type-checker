package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class NonExhaustiveMatchPatternsError(private val expected: Type, val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_NONEXHAUSTIVE_MATCH_PATTERNS:
                для выражения 
                    ${ctx.toStringTree(parser)}
                перечислены не все образцы для типа
                    $expected                
       """.trimIndent()
    }

}

