package dev.ebelekhov.typechecker.types

data class SumType(val inl: Type, val inr: Type) : TopType() {
    override fun toString(): String {
        return "$inl + $inr"
    }
}