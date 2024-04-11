package dev.ebelekhov.typechecker.types

data class FuncType(val argTypes: List<Type>, val returnType: Type) : TopType() {
    override fun toString(): String {
        return "fn(${argTypes.joinToString(separator = ", ")}) -> $returnType"
    }
}