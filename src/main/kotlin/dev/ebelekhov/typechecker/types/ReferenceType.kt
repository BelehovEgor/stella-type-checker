package dev.ebelekhov.typechecker.types

data class ReferenceType(val innerType: Type) : TopType() {
    override fun toString(): String {
        return "&${innerType}"
    }
}