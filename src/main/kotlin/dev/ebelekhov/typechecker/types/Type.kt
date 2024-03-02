package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.errors.BaseError
import dev.ebelekhov.typechecker.errors.UnexpectedTypeForExpressionError
import org.antlr.v4.runtime.RuleContext
import kotlin.reflect.KClass

sealed interface Type {

    fun ensure(expected: Type, ctx: RuleContext) : Type {
        if (this == expected) return this

        throw ExitException(UnexpectedTypeForExpressionError(expected, this, ctx))
    }

    fun ensureOrError(expected: Type, errorFactory: (Type) -> BaseError) : Type {
        if (this == expected) return this

        throw ExitException(errorFactory(this))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Type> ensureOrError(expectedType: KClass<T>, errorFactory: (Type) -> BaseError) : T {
        if (this::class == expectedType) return this as T

        throw ExitException(errorFactory(this))
    }
}

