package dev.ebelekhov.typechecker.types

data class ReferenceType(val innerType: Type) : Type {
    override fun toString(): String {
        return "&${innerType}"
    }
}