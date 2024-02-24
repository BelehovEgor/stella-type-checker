package dev.ebelekhov.typechecker

import java.nio.file.Paths
import kotlin.io.path.readText

fun main() {
    // D:\itmo\2-sem-2023-2024\languages\project\stella-type-checker\src\test\resources\ok\sum_arg.st
    val pathToTheFile = readln()
    val typeValidator = TypeValidator(StellaVisitor())
    typeValidator.accept(Paths.get(pathToTheFile).readText())
}

fun debug() {
    val codeExample = """
language core;

fn increment_twice(n : Nat) -> Nat {
  return succ(succ(n))
}

fn main(n : Nat) -> Nat {
  return increment_twice(n)
}
    """.trimIndent()

    val typeValidator = TypeValidator(StellaVisitor())
    typeValidator.accept(codeExample)
}
