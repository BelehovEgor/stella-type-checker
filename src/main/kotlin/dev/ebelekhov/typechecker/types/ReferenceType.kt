package dev.ebelekhov.typechecker.types

import org.antlr.v4.runtime.RuleContext

data class ReferenceType(val innerType: Type) : Type {
    override fun toString(): String {
        return "&${innerType}"
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        if (other == TopType) return true

        if (other !is ReferenceType) return false

        return this.innerType.isSubtype(other.innerType, ctx) &&
               other.innerType.isSubtype(this.innerType, ctx)
    }
}