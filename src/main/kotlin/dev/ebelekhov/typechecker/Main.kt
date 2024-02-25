package dev.ebelekhov.typechecker

import java.nio.file.Paths
import kotlin.io.path.readText
import kotlin.system.exitProcess

fun main() {
    // D:\itmo\2-sem-2023-2024\languages\project\stella-type-checker\src\test\resources\ok\sum_arg.st
    //val pathToTheFile = readln()
    //val typeValidator = TypeValidator(StellaVisitor())
    //
    //val result = typeValidator.accept(Paths.get(pathToTheFile).readText())
    //
    //if (result.isFailure) {
    //    println((result.exceptionOrNull() as ExitException).error.getMessage())
    //    exitProcess(1)
    //}


    debug()
}

fun debug() {
    val codeExample = """
language core;
extend with #records;

fn iterate(n : Nat) -> { current : Nat, next : Nat } {
  return { current = n, next = succ(n) }
}

fn main(n : Nat) -> Nat {
  return iterate(0).next
}
    """.trimIndent()

    val typeValidator = TypeValidator(StellaVisitor())
    val result = typeValidator.accept(codeExample)

    if (result.isFailure) {
        println((result.exceptionOrNull() as ExitException).error.getMessage())
        exitProcess(1)
    }
}
