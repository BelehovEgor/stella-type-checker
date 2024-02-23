package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaLexer
import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class Parser {
    fun parse(code: String) : stellaParser.ProgramContext {
        val lexer = stellaLexer(CharStreams.fromString(code))
        val tokens = CommonTokenStream(lexer)
        val parser = stellaParser(tokens)

        return parser.program()
    }
}