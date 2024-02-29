package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class UnexpectedPatternForTypeError(
    private val actual: Type,
    private val ctx: RuleContext) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_PATTERN_FOR_TYPE:
                образец
                    ${ctx.toStringTree(parser)}
                не соответствует типу разбираемого выражения
                    $actual
        """.trimIndent()
    }

}
