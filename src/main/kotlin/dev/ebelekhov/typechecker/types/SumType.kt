package dev.ebelekhov.typechecker.types

data class SumType(val inl: Type, val inr: Type) : Type {
    override fun toString(): String {
        return "$inl + $inr"
    }
}