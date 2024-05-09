package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.errors.BaseError
import dev.ebelekhov.typechecker.errors.UnexpectedTypeForExpressionError
import org.antlr.v4.runtime.RuleContext
import kotlin.reflect.KClass

data class AutoType(val constraints: HashSet<Type> = hashSetOf()) : Type {
    override fun toString(): String {
        return "auto ${constraints.joinToString { it.toString() }}"
    }

    override fun ensure(expected: Type, ctx: RuleContext): Type {
        return when(expected) {
            is AutoType -> {
                constraints.addAll(expected.constraints)

                if (constraints.size > 1) {
                    throw ExitException(UnexpectedTypeForExpressionError(expected, this, ctx))
                }

                this
            }
            else -> {
                constraints.add(expected)

                if (constraints.size > 1) {
                    throw ExitException(UnexpectedTypeForExpressionError(expected, this, ctx))
                }

                constraints.first().ensure(expected, ctx)
            }
        }
    }

    override fun ensureOrError(expected: Type, errorFactory: (Type) -> BaseError): Type {
        return when(expected) {
            is AutoType -> {
                constraints.addAll(expected.constraints)

                if (constraints.size > 1) {
                    throw ExitException(errorFactory(this))
                }

                this
            }
            else -> {
                constraints.add(expected)

                if (constraints.size > 1) {
                    throw ExitException(errorFactory(this))
                }

                constraints.first().ensureOrError(expected, errorFactory)
            }
        }
    }

    override fun <T : Type> ensureOrError(expectedType: KClass<T>, errorFactory: (Type) -> BaseError): T {
        if (constraints.size > 1) {
            throw ExitException(errorFactory(this))
        }

        return constraints.first().ensureOrError(expectedType, errorFactory)
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        val constraint = constraints.firstOrNull { !it.isSubtype(other, ctx) }
        if (constraint != null) {
            throw ExitException(UnexpectedTypeForExpressionError(constraint, other, ctx))
        }

        return true
    }

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is AutoType -> {
                if (this.constraints.containsAll(other.constraints)) {
                    other.constraints.addAll(this.constraints)

                    return true
                }

                return false
            }
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}