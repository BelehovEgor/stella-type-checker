package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.errors.BaseError
import dev.ebelekhov.typechecker.errors.ExceptionTypeNotDeclaredError
import dev.ebelekhov.typechecker.errors.UndefinedTypeVariableError
import dev.ebelekhov.typechecker.types.*
import org.antlr.v4.runtime.RuleContext
import kotlin.reflect.KClass

class FuncContext(private val extensions: HashSet<StellaExtension>) {
    private var variables = mutableMapOf<String, MutableList<Type>>()
    private val expectedReturnTypes = mutableListOf<Type?>()
    private var exceptionExpectedType : Type? = null
    private var exceptionVariantTypes = mutableListOf<Pair<String, Type?>>()
    private val generics = mutableMapOf<String, MutableList<VarType>>()

    fun hasExtension(extension: StellaExtension): Boolean {
        return extensions.contains(extension)
    }

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

    fun getDepthOfGeneric(name: String): Int {
        return generics[name]?.size ?: 0
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

    fun runWithGenerics(typeVars: List<VarType>, ctx: RuleContext, action: () -> Type): Type {
        typeVars.forEach {
            if (generics.contains(it.name)) {
                generics[it.name]!!.add(it)
            } else {
                generics[it.name] = mutableListOf(it)
            }
        }

        try {
            val type = action()

            val undefined = GenericFuncType.getVars(type).firstOrNull {
                !generics.contains(it.name)
            }
            if (undefined != null) {
                throw ExitException(UndefinedTypeVariableError(undefined, ctx))
            }

            return type
        }
        finally {
            typeVars.forEach {
                generics[it.name]!!.removeLast()
            }
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

            return ensureWithContext(returnType, expectedReturnType, ctx)
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
        val expected = expectedReturnTypes.lastOrNull()

        if (extensions.contains(StellaExtension.AmbiguousTypeAsBottom) && expected == null) {
            return BotType()
        }

        return expected
    }

    fun  <T : Type> getCurrentExpectedReturnType(expectedType: KClass<T>, errorFactory: (Type) -> BaseError) : Type? {
        val expected = expectedReturnTypes.lastOrNull()

        if (extensions.contains(StellaExtension.TypeReconstruction) &&
            ((expected is AutoType && expected.constraint == null) || expected == null)) {
            return expected ?: AutoType()
        }

        if (extensions.contains(StellaExtension.AmbiguousTypeAsBottom) && expected == null) {
            return BotType()
        }

        if (expected == null) return null

        if (extensions.contains(StellaExtension.StructuralSubtyping) &&
            (expected is TopType || expected::class == expectedType)) {
             return expected
        }

        return expected.ensureOrError(expectedType, errorFactory)
    }

    fun ensureOrErrorWithContext(actual: Type, expected: Type, ctx: RuleContext, errorFactory: (Type) -> BaseError) : Type {
        if (extensions.contains(StellaExtension.StructuralSubtyping)) {
            return expected.ensureSubtype(actual, ctx)
        }

        return actual.ensureOrError(expected, errorFactory)
    }

    fun ensureWithContext(actual: Type, expected: Type, ctx: RuleContext) : Type {
        if (extensions.contains(StellaExtension.StructuralSubtyping)) {
            actual.ensureSubtype(expected, ctx)

            return expected
        }

        return actual.ensure(expected, ctx)
    }

    private fun removeVariable(variableName: String) {
        val varTypes = variables.getOrDefault(variableName, null) ?: return

        varTypes.removeLast()
        if (varTypes.isEmpty()) variables.remove(variableName)
    }
}