package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.errors.BaseError
import dev.ebelekhov.typechecker.errors.UnexpectedTypeForExpressionError
import kotlin.reflect.KClass

sealed interface Type {

    fun ensure(expected: Type, expression: stellaParser.ExprContext) : Type {
        if (this == expected) return this

        throw ExitException(UnexpectedTypeForExpressionError(expected, this, expression))
    }

    fun ensureOrError(expected: Type, errorFactory: (Type) -> BaseError) : Type {
        if (this == expected) return this

        throw ExitException(errorFactory(this))
    }

    fun <T : Type> ensureOrError(expectedType: KClass<T>, errorFactory: (Type) -> BaseError) : T {
        if (this::class == expectedType) return this as T

        throw ExitException(errorFactory(this))
    }
}

