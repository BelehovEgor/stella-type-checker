package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaLexer
import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.antlr.parser.stellaParserVisitor
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class TypeValidator(private val visitor: stellaParserVisitor<Type>) {
    fun accept(codeExample: String) : Result<Unit> {
        val parser = getParser(codeExample)

        return try {
            parser.program().accept(visitor)

            Result.success(Unit)
        } catch (exc: ExitException) {
            Result.failure(exc)
        }
    }

    private fun getParser(code: String) : stellaParser {
        val lexer = stellaLexer(CharStreams.fromString(code))
        val tokens = CommonTokenStream(lexer)

        return stellaParser(tokens)
    }
}