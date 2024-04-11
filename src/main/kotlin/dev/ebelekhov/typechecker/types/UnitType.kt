package dev.ebelekhov.typechecker.types

data object UnitType : TopType() {
    override fun toString(): String {
        return "Unit"
    }
}