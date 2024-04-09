package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.errors.ExceptionTypeNotDeclaredError
import dev.ebelekhov.typechecker.types.ErrorType
import dev.ebelekhov.typechecker.types.Type
import dev.ebelekhov.typechecker.types.VariantType
import org.antlr.v4.runtime.RuleContext

class FuncContext {
    private var variables = mutableMapOf<String, MutableList<Type>>()
    private val expectedReturnTypes = mutableListOf<Type?>()
    private var exceptionExpectedType : Type? = null
    private var exceptionVariantTypes = mutableListOf<Pair<String, Type?>>()

    fun addExceptionExpectedType(type: Type) {
        exceptionExpectedType = type
    }

    fun addExceptionVariantPair(name: String, type: Type) {
        exceptionVariantTypes.add(Pair(name, type))
    }

    fun getExceptionExpectedType() : Type {
        if (exceptionExpectedType != null)
            return exceptionExpectedType!!

        if (exceptionVariantTypes.isNotEmpty())
            return VariantType(exceptionVariantTypes)

        throw ExitException(ExceptionTypeNotDeclaredError())
    }

    fun runWithVariable(name : String,
                        type: Type,
                        action: () -> Type): Type {
        addVariable(name, type)
        try {
            return action()
        } finally {
            removeVariable(name)
        }
    }

    fun runWithVariables(variables : List<Pair<String, Type>>,
                         action: () -> Type): Type {
        variables.forEach { addVariable(it.first, it.second) }
        try {
            return action()
        } finally {
            variables.forEach { removeVariable(it.first) }
        }
    }

    fun runWithScope(action: () -> Type): Type {
        val copyState = variables.map { (key, value) -> key to value.toMutableList()}.toMap()
        try {
            return action()
        } finally {
            variables = copyState.toMutableMap()
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

            if (returnType !is ErrorType) returnType.ensure(expectedReturnType, ctx)

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