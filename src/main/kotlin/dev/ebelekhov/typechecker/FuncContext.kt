package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class FuncContext {
    private var variables = mutableMapOf<String, MutableList<Type>>()
    private val expectedReturnTypes = mutableListOf<Type?>()

    fun runWithVariables(variables : List<Pair<String, Type>>,
                         action: () -> Type): Type {
        variables.forEach { addVariable(it.first, it.second) }
        try {
            return action()
        } finally {
            variables.forEach { removeVariable(it.first) }
        }
    }

    fun runWithPatternVariable(action: () -> Type): Type {
        val copyState = variables.toMutableMap()

        try {
            return action()
        } finally {
            variables = copyState
        }
    }

    fun getVariableType(variableName: String): Type? {
        val varTypes = variables.getOrDefault(variableName, null) ?: return null

        return varTypes.lastOrNull()
    }

    fun addVariable(variableName: String, type: Type) {
        variables.getOrPut(variableName){ mutableListOf() }.add(type)
    }

    fun runWithExpectedReturnType(expectedReturnType: Type,
                                  ctx: RuleContext,
                                  action: () -> Type): Type {
        expectedReturnTypes.add(expectedReturnType)

        try {
            val returnType = action()
            returnType.ensure(expectedReturnType, ctx)

            return returnType
        }
        finally {
            expectedReturnTypes.removeLast()
        }
    }

    fun runWithPatternReturnType(expectedReturnType: Type,
                                  action: () -> Type): Type {
        expectedReturnTypes.add(expectedReturnType)

        try {
            return action()
        }
        finally {
            expectedReturnTypes.removeLast()
        }
    }

    fun runWithoutExpectations(action: () -> Type): Type {
        expectedReturnTypes.add(null)

        try {
            return action()
        }
        finally {
            expectedReturnTypes.removeLast()
        }
    }

    fun getCurrentExpectedReturnType() : Type? {
        return expectedReturnTypes.lastOrNull()
    }

    private fun removeVariable(variableName: String) {
        val varTypes = variables.getOrDefault(variableName, null) ?: return

        varTypes.removeLast()
        if (varTypes.isEmpty()) variables.remove(variableName)
    }
}