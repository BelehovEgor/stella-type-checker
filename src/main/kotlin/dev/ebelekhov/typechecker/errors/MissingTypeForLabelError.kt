package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class MissingTypeForLabelError(
    private val type: Type,
    private val ctx: RuleContext
) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_MISSING_TYPE_FOR_LABEL
                missing type for a variant label failure
                with expected type
                    $type
        """.trimIndent()
    }
}