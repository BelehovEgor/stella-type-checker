package dev.ebelekhov.typechecker.types

data class RecordType(val fields: Map<String, Type>) : Type {
    override fun toString(): String {
        return "{${fields.entries.joinToString { "${it.key} : ${it.value}" }}}"
    }
}