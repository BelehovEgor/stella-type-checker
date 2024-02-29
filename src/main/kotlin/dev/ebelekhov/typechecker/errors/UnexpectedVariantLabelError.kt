package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import dev.ebelekhov.typechecker.types.VariantType

data class UnexpectedVariantLabelError(
    val label: String,
    val labelType: Type,
    val actual: VariantType,
    val expression: stellaParser.ExprContext) : BaseError() {

    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_VARIANT_LABEL:
                в выражении
                    ${expression.toStringTree(parser)}
                неожиданная метка 
                    $label : $labelType
                для типа варианта
                    $actual
       """.trimIndent()
    }
}