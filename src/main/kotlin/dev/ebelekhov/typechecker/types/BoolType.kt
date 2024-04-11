package dev.ebelekhov.typechecker.types

data object BoolType : TopType() {
    override fun toString(): String {
        return "Bool"
    }

    override fun isSubtype(other: Type): Boolean {
        return other is BoolType
    }
}