package dev.ebelekhov.typechecker.types

data class TupleType(val types: List<Type>) : Type {
    override fun toString(): String {
        return "{${types.joinToString(separator = ", ")}}"
    }
}