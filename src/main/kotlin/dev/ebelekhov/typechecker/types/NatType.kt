package dev.ebelekhov.typechecker.types

data object NatType : TopType() {
    override fun toString(): String {
        return "Nat"
    }
}