package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class NonExhaustiveMatchPatternsError(private val expectedType: Type, val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_NONEXHAUSTIVE_MATCH_PATTERNS:
                для выражения 
                    ${expression.text}
                перечислены не все образцы для типа
                    $expectedType                
       """.trimIndent()
    }

}

