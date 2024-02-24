package dev.ebelekhov.typechecker

fun main() {
    val codeExample = """
        language core;

        fn func(n : Nat) -> Nat {
          return n;
        }
    """.trimIndent()

    val typeValidator = TypeValidator(StellaVisitor())
    typeValidator.accept(codeExample)
}