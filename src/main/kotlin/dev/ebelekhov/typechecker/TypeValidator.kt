package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.antlr.parser.stellaParserVisitor
import dev.ebelekhov.typechecker.types.Type

class TypeValidator(
    private val parser: stellaParser,
    private val visitor: stellaParserVisitor<Type>) {
    fun accept() : Result<Unit> {
        return try {
            parser.program().accept(visitor)

            Result.success(Unit)
        } catch (exc: ExitException) {
            Result.failure(exc)
        }
    }
}