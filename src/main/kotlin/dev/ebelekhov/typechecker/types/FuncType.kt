package dev.ebelekhov.typechecker.types

import org.antlr.v4.runtime.RuleContext

data class FuncType(val argTypes: List<Type>, val returnType: Type) : Type {
    override fun toString(): String {
        return "fn(${argTypes.joinToString(separator = ", ")}) -> $returnType"
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        if (other == TopType) return true

        if (other !is FuncType) return false

        if (!other.returnType.isSubtype(this.returnType, ctx)) return false

        return this.argTypes.withIndex().all { it.value.isSubtype(other.argTypes[it.index], ctx) }
    }
}