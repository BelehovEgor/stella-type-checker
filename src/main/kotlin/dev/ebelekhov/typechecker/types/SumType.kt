package dev.ebelekhov.typechecker.types

import org.antlr.v4.runtime.RuleContext

data class SumType(val inl: Type, val inr: Type) : Type {
    override fun toString(): String {
        return "$inl + $inr"
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        if (other == TopType) return true

        if (other !is SumType) return false

        return this.inl.isSubtype(other.inl, ctx) && this.inr.isSubtype(other.inr, ctx)
    }
}