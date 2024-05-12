package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.errors.BaseError

data class VarType(val name: String, val depth: Int) : Type {
    override fun toString(): String {
        return "${name}_$depth"
    }

    override fun ensureOrError(expected: Type, errorFactory: (Type) -> BaseError): Type {
        if (expected is VarType && (expected.name != this.name || expected.depth != this.depth))
            throw ExitException(errorFactory.invoke(expected))

        return expected
    }
}



