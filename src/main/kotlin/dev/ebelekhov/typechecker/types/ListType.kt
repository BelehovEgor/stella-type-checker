package dev.ebelekhov.typechecker.types

data class ListType(val type: Type) : TopType() {
    override fun toString(): String {
        return "[$type]"
    }
}