package dev.ebelekhov.typechecker.types

data class ErrorType(val type: Type) : TopType() {
    override fun toString(): String {
        return "throw (${type})"
    }
}