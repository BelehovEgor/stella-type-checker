package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class NonExhaustiveMatchPatternsError(private val expected: Type, val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_NONEXHAUSTIVE_MATCH_PATTERNS:
                для выражения 
                    ${expression.toStringTree(parser)}
                перечислены не все образцы для типа
                    $expected                
       """.trimIndent()
    }

}

