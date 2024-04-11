package dev.ebelekhov.typechecker.types

data class TupleType(val types: List<Type>) : TopType() {
    override fun toString(): String {
        return "{${types.joinToString(separator = ", ")}}"
    }
}