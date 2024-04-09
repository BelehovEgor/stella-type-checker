package dev.ebelekhov.typechecker.types

data class ErrorType(val type: Type) : Type {
    override fun toString(): String {
        return "throw (${type})"
    }
}