package dev.ebelekhov.typechecker.types

data object NatType : Type {
    override fun toString(): String {
        return "Nat"
    }
}