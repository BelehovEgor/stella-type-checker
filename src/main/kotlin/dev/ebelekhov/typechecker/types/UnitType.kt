package dev.ebelekhov.typechecker.types

data object UnitType : Type {
    override fun toString(): String {
        return "Unit"
    }
}