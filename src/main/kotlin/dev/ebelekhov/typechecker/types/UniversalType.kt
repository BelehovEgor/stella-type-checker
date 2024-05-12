package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.errors.BaseError

data class UniversalType(
    val generics: List<VarType>,
    val nestedType: Type
) : Type {
    override fun toString(): String {
        return generics.joinToString { "forall $it. " } + nestedType.toString()
    }

    override fun ensureOrError(expected: Type, errorFactory: (Type) -> BaseError): Type {
        return nestedType.ensureOrError(expected, errorFactory)
    }
}