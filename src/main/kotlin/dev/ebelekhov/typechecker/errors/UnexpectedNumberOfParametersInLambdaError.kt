package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.RuleContext

class UnexpectedNumberOfParametersInLambdaError(val expected: Int, val ctx: RuleContext) :
    BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_NUMBER_OF_PARAMETERS_IN_LAMBDA:
                количество параметров анонимной функции
                    ${ctx.toStringTree(parser)}
                не совпадает с ожидаемым количеством параметров 
                    $expected
        """.trimIndent()
    }
}