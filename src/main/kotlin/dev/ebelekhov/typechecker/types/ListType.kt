package dev.ebelekhov.typechecker.types

import org.antlr.v4.runtime.RuleContext

data class ListType(val type: Type) : Type {
    override fun toString(): String {
        return "[$type]"
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        if (other == TopType) return true

        if (other !is ListType) return false

        return this.type.isSubtype(other.type, ctx)
    }
}