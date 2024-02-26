package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import dev.ebelekhov.typechecker.types.VariantType

data class UnexpectedVariantLabelError(
    val label: String,
    val labelType: Type,
    val type: VariantType,
    val expression: stellaParser.ExprContext) : BaseError() {

    override fun getMessage(): String {
        return """
            ERROR_UNEXPECTED_VARIANT_LABEL:
                в выражении
                    ${expression.text}
                неожиданная метка 
                    $label : $labelType
                для типа варианта
                    $type
       """.trimIndent()
    }
}