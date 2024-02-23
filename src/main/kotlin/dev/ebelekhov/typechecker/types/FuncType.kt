package dev.ebelekhov.typechecker.types

data class FuncType(val argType: Type, val returnType: Type) : Type {
    override fun toString(): String {
        return "fn($argType) -> $returnType"
    }
}