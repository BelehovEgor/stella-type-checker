package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
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

        return commonFields.all {
            it.first.second == null
                    && it.second!!.second == null ||
            it.first.second != null
                    && it.second!!.second != null
                    && it.first.second!!.isSubtype(it.second!!.second!!, ctx)
        }
    }
}
