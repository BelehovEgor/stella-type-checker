package dev.ebelekhov.typechecker.types

data class RecordType(val fields: List<Pair<String, Type>>) : TopType() {
    override fun toString(): String {
        return "{${fields.joinToString { "${it.first} : ${it.second}" }}}"
    }

    fun getTypeByName(name: String) : Type? {
        return fields.firstOrNull { it.first == name }?.second
    }
}