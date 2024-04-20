package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.errors.MissingTypeForLabelError
import dev.ebelekhov.typechecker.errors.UnexpectedTypeForNullaryLabelError
import dev.ebelekhov.typechecker.errors.UnexpectedVariantLabelError
import org.antlr.v4.runtime.RuleContext

data class VariantType(val variants: List<Pair<String, Type?>>) : Type {
    override fun toString(): String {
        return "<| ${variants.joinToString { "${it.first} : ${it.second}" }} |>"
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        if (other == TopType) return true

        if (other !is VariantType) return false

        val commonFields = other.variants
            .map { x -> Pair(x, this.variants.firstOrNull { y -> x.first == y.first }) }
            .filter { it.second != null }
        if (commonFields.count() != this.variants.count()) {
            val field = this.variants.first { x -> commonFields.all { y -> x.first != y.first.first } }

            throw ExitException(UnexpectedVariantLabelError(field.first, other, ctx))
        }

        if (commonFields.any { it.first.second != null && it.second?.second == null }) {
            val field = commonFields.first { it.first.second != null && it.second?.second == null }

            throw ExitException(MissingTypeForLabelError(field.first.second!!, ctx))
        }

        if (commonFields.any { it.first.second == null && it.second?.second != null }) {
            val field = commonFields.first { it.first.second == null && it.second?.second != null }

            throw ExitException(UnexpectedTypeForNullaryLabelError(field.second!!.second!!, ctx))
        }

        return commonFields.all {
            it.first.second == null
                    && it.second!!.second == null ||
            it.first.second != null
                    && it.second!!.second != null
                    && it.first.second!!.isSubtype(it.second!!.second!!, ctx)
        }
    }
}
