package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.errors.UnexpectedTupleLengthError
import org.antlr.v4.runtime.RuleContext

data class TupleType(val types: List<Type>) : Type {
    override fun toString(): String {
        return "{${types.joinToString(separator = ", ")}}"
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        if (other == TopType) return true

        if (other !is TupleType) return false

        if (other.types.count() != this.types.count())
            throw ExitException(UnexpectedTupleLengthError(other, ctx))

        return this.types.withIndex().all {
            it.value.isSubtype(other.types[it.index], ctx)
        }
    }
}