package dev.ebelekhov.typechecker.types

data object BoolType : Type {
    override fun toString(): String {
        return "Bool"
    }
}
