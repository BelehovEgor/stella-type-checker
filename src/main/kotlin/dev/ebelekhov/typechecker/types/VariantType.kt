package dev.ebelekhov.typechecker.types

data class VariantType(val variants: List<Pair<String, Type?>>) : Type {
    override fun toString(): String {
        return "<| ${variants.joinToString { "${it.first} : ${it.second}" }} |>"
    }
}
