package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.errors.BaseError
import dev.ebelekhov.typechecker.errors.OccursCheckInfiniteTypeError
import dev.ebelekhov.typechecker.errors.UnexpectedTypeForExpressionError
import org.antlr.v4.runtime.RuleContext
import kotlin.reflect.KClass

data class AutoType(var constraint: Type? = null) : Type {
    override fun toString(): String {
        return "auto ${constraint ?: ""}"
    }

    override fun ensure(expected: Type, ctx: RuleContext): Type {
        return when(expected) {
            is AutoType -> {
                if (constraint != null && expected.constraint != null && constraint != expected.constraint) {
                    throw ExitException(UnexpectedTypeForExpressionError(expected, this, ctx))
                }

                expected.constraint = expected.constraint ?: constraint
                constraint = constraint ?: expected.constraint

                expected
            }
            else -> {
                if (constraint != null && constraint != expected) {
                    throw ExitException(UnexpectedTypeForExpressionError(expected, this, ctx))
                }

                constraint = constraint ?: expected

                this
            }
        }
    }

    override fun ensureOrError(expected: Type, errorFactory: (Type) -> BaseError): Type {
        return when(expected) {
            is AutoType -> {
                if (constraint != null && expected.constraint != null && constraint != expected.constraint) {
                    throw ExitException(errorFactory(this))
                }

                expected.constraint = expected.constraint ?: constraint
                constraint = constraint ?: expected.constraint

                expected
            }
            else -> {
                if (constraint != null && constraint != expected) {
                    throw ExitException(errorFactory(this))
                }

                constraint = constraint ?: expected

                this
            }
        }
    }

    override fun <T : Type> ensureOrError(expectedType: KClass<T>, errorFactory: (Type) -> BaseError): T {
        if (constraint != null) return constraint!!.ensureOrError(expectedType, errorFactory)

        return when(expectedType) {
            ListType::class -> {
                constraint = ListType(AutoType())

                constraint as T
            }
            else -> TODO()
        }
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        if (constraint == null) {
            constraint = other
        }

        return constraint!!.isSubtype(other, ctx)
    }

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is AutoType -> {
                if (other.constraint == null || this.constraint == other.constraint) {
                    other.constraint = this.constraint

                    return true
                }

                return false
            }
            else -> constraint?.equals(other) ?: true
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}