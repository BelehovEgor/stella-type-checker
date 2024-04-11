package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class TypeValidator(
    private val parser: stellaParser) {
    fun accept() : Result<Unit> {
        return try {
            val visitor = StellaVisitor(FuncContext(listOf()))

            parser.program().accept(visitor)

            Result.success(Unit)
        } catch (exc: ExitException) {
            Result.failure(exc)
        }
    }


}