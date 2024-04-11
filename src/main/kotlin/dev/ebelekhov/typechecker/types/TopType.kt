package dev.ebelekhov.typechecker.types

open class TopType : Type {
    override fun toString(): String {
        return "top"
    }

    open fun isSubtype(other: Type): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        return true
    }

    override fun hashCode(): Int {
        return 123
    }
}