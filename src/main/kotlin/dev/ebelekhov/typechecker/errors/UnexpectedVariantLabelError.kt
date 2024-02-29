package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import dev.ebelekhov.typechecker.types.VariantType
import org.antlr.v4.runtime.RuleContext

data class UnexpectedVariantLabelError(
    val label: String,
    val labelType: Type,
    val actual: VariantType,
    val ctx: RuleContext) : BaseError() {

    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_VARIANT_LABEL:
                в выражении
                    ${ctx.toStringTree(parser)}
                неожиданная метка 
                    $label : $labelType
                для типа варианта
                    $actual
       """.trimIndent()
    }
}