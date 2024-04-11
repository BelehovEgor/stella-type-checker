package dev.ebelekhov.typechecker.types

import org.antlr.v4.runtime.RuleContext

data class ErrorType(val type: Type) : Type {
    override fun toString(): String {
        return "throw (${type})"
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        return true
    }
}