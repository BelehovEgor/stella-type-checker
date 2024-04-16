package dev.ebelekhov.typechecker.types

import dev.ebelekhov.typechecker.ExitException
import dev.ebelekhov.typechecker.errors.UnexpectedRecordFieldsError
import org.antlr.v4.runtime.RuleContext

data class RecordType(val fields: List<Pair<String, Type>>) : Type {
    override fun toString(): String {
        return "{${fields.joinToString { "${it.first} : ${it.second}" }}}"
    }

    fun getTypeByName(name: String) : Type? {
        return fields.firstOrNull { it.first == name }?.second
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        if (other == TopType) return true

        if (other !is RecordType) return false

        val commonFields = other.fields
            .map { x -> Pair(x, this.fields.firstOrNull { y -> x.first == y.first }) }
            .filter { it.second != null }
        if (commonFields.count() != other.fields.count()) {
            throw ExitException(UnexpectedRecordFieldsError(this, other, ctx))
        }

        return commonFields.all {
            it.first.second.isSubtype(it.second!!.second, ctx)
        }
    }
}