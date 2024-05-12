package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.errors.BaseError
import org.antlr.v4.runtime.RuleContext
import kotlin.reflect.KClass

data class AutoType(var constraint: Type? = null) : Type {
    private val number : Int = ++counter

    override fun toString(): String {
        return when(constraint) {
            null -> "auto T$number"
            else -> "auto T$number($constraint)"
        }
    }

    override fun ensureOrError(expected: Type, errorFactory: (Type) -> BaseError): Type {
        val lastCurrentAuto = getLast(this)
        val thisConstraint = getInternalConstraint(lastCurrentAuto)
        val expectedConstraint = getInternalConstraint(expected)

        if (thisConstraint != null && expectedConstraint != null && thisConstraint != expectedConstraint) {
            throw ExitException(errorFactory(expected))
        }

        if (thisConstraint == null) {
            setIfNoConstraint(this, expected)
        }
        else if (expectedConstraint == null && expected is AutoType) {
            setIfNoConstraint(expected, this)
        }

        return this
    }

    override fun <T : Type> ensureOrError(expectedType: KClass<T>, errorFactory: (Type) -> BaseError): T {
        if (constraint != null) return constraint!!.ensureOrError(expectedType, errorFactory)

        return when(expectedType) {
            ListType::class -> {
                constraint = ListType(AutoType())

                constraint as T
            }
            SumType::class -> {
                constraint = SumType(AutoType(), AutoType())

                constraint as T
            }
            else -> TODO()
        }
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        TODO("Unsupported")
    }

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is AutoType -> {
                val thisConstraint = getInternalConstraint(this)
                val expectedConstraint = getInternalConstraint(other)

                if (expectedConstraint == null || thisConstraint == expectedConstraint) {
                    setIfNoConstraint(other, this)

                    return true
                }

                return false
            }
            else -> constraint?.equals(other) ?: true
        }
    }

    override fun hashCode(): Int {
        var result = constraint?.hashCode() ?: 0
        result = 31 * result + number
        return result
    }

    private companion object {
        var counter : Int = 0

        fun getInternalConstraint(auto : Type?) : Type? {
            return if (auto is AutoType)
                getInternalConstraint(auto.constraint)
            else
                auto
        }

        fun setIfNoConstraint(current : AutoType, other : Type?) {
            when(other) {
                null -> return
                is AutoType -> {
                    val currentLast = getLast(current)
                    val otherLast = getLast(other)

                    if (currentLast.constraint == null &&
                        (otherLast.constraint != null || otherLast.number != currentLast.number)) {
                        currentLast.constraint = otherLast
                    }
                }
                else -> {
                    getLast(current).constraint = other
                }
            }
        }

        fun getLast(auto : AutoType) : AutoType {
            return if (auto.constraint is AutoType)
                getLast(auto.constraint as AutoType)
            else
                auto
        }
    }
}