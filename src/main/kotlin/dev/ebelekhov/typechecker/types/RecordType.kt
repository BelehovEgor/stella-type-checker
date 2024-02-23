package dev.ebelekhov.typechecker.types

data class RecordType(val fields: List<Pair<String, Type>>) : Type {
    override fun toString(): String {
        return "{${fields.joinToString(separator = ", ") { "${it.first} : ${it.second}" }}}"
    }
}