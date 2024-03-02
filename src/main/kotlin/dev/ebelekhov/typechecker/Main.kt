package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaLexer
import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import kotlin.system.exitProcess

fun main() {
    debug()

    // run()
}

fun run() {
    val code = readCode()

    val parser = getParser(code)
    val typeValidator = TypeValidator(parser, StellaVisitor())
    val result = typeValidator.accept()

    if (result.isFailure) {
        println((result.exceptionOrNull() as ExitException).error.getMessage(parser))
        exitProcess(1)
    }
}

fun readCode() : String {
    val code = StringBuilder()
    var line = readlnOrNull()
    while (line != null) {
        code.append(line)
        line = readlnOrNull()
    }

    return code.toString()
}

fun getParser(code: String) : stellaParser {
    val lexer = stellaLexer(CharStreams.fromString(code))
    val tokens = CommonTokenStream(lexer)

    return stellaParser(tokens)
}

fun debug() {
    val codeExample = """
language core;

extend with #structural-patterns, #natural-literals, #sum-types;

fn main(n : Nat + Bool) -> Nat {
  return match n {
    	inl(0) => 0
    | inr(b) => 0
   }
}
    """.trimIndent()

    val parser = getParser(codeExample)
    val typeValidator = TypeValidator(parser, StellaVisitor())
    val result = typeValidator.accept()

    if (result.isFailure) {
        println((result.exceptionOrNull() as ExitException).error.getMessage(parser))
        exitProcess(1)
    }
}
