package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class OccursCheckInfiniteTypeError(
    private val actual: Type,
    private val expected: Type,
    private val ctx: RuleContext
) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_OCCURS_CHECK_INFINITE_TYPE:
                для выражения
                    ${ctx.toStringTree(parser)}
                не получается сконструировать бесконечный тип когда
                    $actual
                должен идентифицировать
                    $expected
       """.trimIndent()
    }
}