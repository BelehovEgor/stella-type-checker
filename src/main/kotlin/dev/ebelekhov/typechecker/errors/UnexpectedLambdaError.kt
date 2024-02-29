package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class UnexpectedLambdaError(private val actual: Type, private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_LAMBDA
                для выражения
                    ${ctx.toStringTree(parser)}
                ожидается не функциональный тип
                но получен
                    $actual
        """.trimIndent()
    }

}

