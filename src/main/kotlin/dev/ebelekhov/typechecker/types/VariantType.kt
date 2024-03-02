package dev.ebelekhov.typechecker.types

data class VariantType(val variants: Map<String, Type?>) : Type {
    override fun toString(): String {
        return "<| ${variants.entries.joinToString { "${it.key} : ${it.value}" }} |>"
    }
}