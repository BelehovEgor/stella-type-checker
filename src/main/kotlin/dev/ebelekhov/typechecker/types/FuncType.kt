package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.errors.IncorrectNumberOfArgumentsError
import org.antlr.v4.runtime.RuleContext

data class FuncType(val argTypes: List<Type>, val returnType: Type) : Type {
    override fun toString(): String {
        return "fn(${argTypes.joinToString(separator = ", ")}) -> $returnType"
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        if (other == TopType) return true

        if (other !is FuncType) return false

        if (!this.returnType.isSubtype(other.returnType, ctx)) return false

        if (other.argTypes.count() != this.argTypes.count()) {
            throw ExitException(IncorrectNumberOfArgumentsError(this.argTypes.count(), other.argTypes.count(), ctx))
        }

        return other.argTypes.withIndex().all { it.value.isSubtype(this.argTypes[it.index], ctx) }
    }
}

