package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

data class UnexpectedInjectionError(val expectedType: Type, val expression: stellaParser.ExprContext) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_INJECTION:
                получена инъекция 
                    ${expression.text}
                но ожидается не тип-сумма
                    $expectedType
       """.trimIndent()
    }
}