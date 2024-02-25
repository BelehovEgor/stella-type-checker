package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type

class FuncContext {
    private val variables = mutableMapOf<String, MutableList<Type>>()
    private val expectedReturnTypes = mutableListOf<Type?>()

    fun runWithVariable(variable: String,
                        type: Type,
                        action: () -> Type): Type {
        addVariable(variable, type)
        try {
            return action()
        } finally {
            removeVariable(variable)
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
                                  expression: stellaParser.ExprContext,
                                  action: () -> Type): Type {
        expectedReturnTypes.add(expectedReturnType)

        try {
            val returnType = action()
            returnType.ensure(expectedReturnType, expression)

            return returnType
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