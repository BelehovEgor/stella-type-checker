package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class TypeValidator(
    private val parser: stellaParser) {
    fun accept() : Result<Unit> {
        return try {
            val program = parser.program()
            val extensions = program.getExtensions()

            val visitor = StellaVisitor(FuncContext(extensions))

            program.accept(visitor)

            Result.success(Unit)
        } catch (exc: ExitException) {
            Result.failure(exc)
        }
    }

    private fun stellaParser.ProgramContext.getExtensions(): List<StellaExtension> {
        val anExtensionContext = extension()
        return anExtensionContext
            .filterIsInstance<stellaParser.AnExtensionContext>()
            .flatMap { it.extensionNames }
            .map { StellaExtension.fromString(it.text) }
    }
}