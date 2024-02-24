package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.errors.UnexpectedTypeForExpressionError

sealed interface Type {
    fun ensure(expected: Type, expression: stellaParser.ExprContext) {
        if (this == expected) return

        throw ExitException(UnexpectedTypeForExpressionError(expected, this, expression))
    }
}

