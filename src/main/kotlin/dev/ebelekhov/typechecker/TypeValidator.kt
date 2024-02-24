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
        try {
            parse(codeExample).accept(visitor)
        }
        catch (exc: ExitException) {
            println(exc.message)
            exitProcess(1)
        }
    }

    private fun parse(code: String) : stellaParser.ProgramContext {
        val lexer = stellaLexer(CharStreams.fromString(code))
        val tokens = CommonTokenStream(lexer)
        val parser = stellaParser(tokens)

        return parser.program()
    }
}