package dev.ebelekhov.typechecker.types

data class FuncType(val argTypes: List<Type>, val returnType: Type) : Type {
    override fun toString(): String {
        return "fn(${argTypes.joinToString(separator = ", ")}) -> $returnType"
    }
}