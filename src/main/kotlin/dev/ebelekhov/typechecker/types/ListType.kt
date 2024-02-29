package dev.ebelekhov.typechecker.types

data class ListType(val type: Type) : Type {
    override fun toString(): String {
        return "[$type]"
    }
}