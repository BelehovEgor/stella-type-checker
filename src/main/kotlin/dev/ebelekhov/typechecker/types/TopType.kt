package dev.ebelekhov.typechecker.types

data object TopType : Type {
    override fun toString(): String {
        return "Top"
    }
}