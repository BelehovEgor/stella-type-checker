package dev.ebelekhov.typechecker.types

data class GenericFuncType(val generics : List<VarType>, val funcType: FuncType) : Type {
    override fun toString(): String {
        return "fn<${generics.joinToString(separator = ", ")}>(${funcType.argTypes.joinToString(separator = ", ")}) -> ${funcType.returnType}"
    }

    fun toFuncType(types : List<Type>) : FuncType {
        val funcArgs = funcType.argTypes.map {
            var newArgType = it

            types.forEachIndexed { index, type -> newArgType = changeVarToType(newArgType, generics[index], type) }

            newArgType
        }

        var funcReturnType = funcType.returnType
        types.forEachIndexed { index, type -> funcReturnType = changeVarToType(funcReturnType, generics[index], type) }

        return FuncType(funcArgs, funcReturnType)
    }

    companion object {
        fun getVars(x : Type?) : List<VarType> {
            return when(x) {
                is FuncType -> x.argTypes.flatMap { getVars(it) } + getVars(x.returnType)
                is GenericFuncType -> getVars(x.funcType).filter {
                    !x.generics.contains(it)
                }
                is ListType -> getVars(x.type)
                is RecordType -> x.fields.flatMap { getVars(it.second) }
                is ReferenceType -> getVars(x.innerType)
                is SumType -> getVars(x.inr) + getVars(x.inl)
                is TupleType -> x.types.flatMap { getVars(it) }
                is VarType -> listOf(x)
                is VariantType -> x.variants.flatMap { getVars(it.second) }
                else -> listOf()
            }
        }

        fun changeVarToType(x : Type, compare : VarType, newType : Type) : Type {
            return when(x) {
                is FuncType -> FuncType(
                    x.argTypes.map { changeVarToType(it, compare, newType) },
                    changeVarToType(x.returnType, compare, newType))
                is ListType -> ListType(changeVarToType(x.type, compare, newType))
                is RecordType -> RecordType(x.fields.map { Pair(it.first, changeVarToType(it.second, compare, newType)) })
                is ReferenceType -> ReferenceType(changeVarToType(x.innerType, compare, newType))
                is SumType -> SumType(changeVarToType(x.inl, compare, newType), changeVarToType(x.inr, compare, newType))
                is TupleType -> TupleType(x.types.map { changeVarToType(it, compare, newType) })
                is VarType -> if (x == compare) newType else x
                is VariantType -> VariantType(x.variants.map {
                    Pair(
                        it.first,
                        if (it.second == null)
                            null
                        else
                            changeVarToType(it.second!!, compare, newType))
                })
                is GenericFuncType -> if (x.generics.contains(compare)) x
                    else GenericFuncType(x.generics, changeVarToType(x.funcType, compare, newType) as FuncType)
                is UniversalType -> if (x.generics.contains(compare)) x
                    else changeVarToType(x.nestedType, compare, newType)
                else -> x
            }
        }
    }
}