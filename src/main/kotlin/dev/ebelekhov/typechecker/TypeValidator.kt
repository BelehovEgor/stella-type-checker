package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaLexer
import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.antlr.parser.stellaParserVisitor
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import kotlin.system.exitProcess

class TypeValidator(private val visitor: stellaParserVisitor<Type>) {
    fun accept(codeExample: String) {
        val parser = getParser(codeExample)

        try {
            parser.program().accept(visitor)
        }
        catch (exc: ExitException) {
            println(exc.error.getMessage())
            exitProcess(1)
        }
    }

    private fun getParser(code: String) : stellaParser {
        val lexer = stellaLexer(CharStreams.fromString(code))
        val tokens = CommonTokenStream(lexer)

        return stellaParser(tokens)
    }
}