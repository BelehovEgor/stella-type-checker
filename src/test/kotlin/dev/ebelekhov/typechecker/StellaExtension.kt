package dev.ebelekhov.typechecker

enum class StellaExtension(val extensionName: String, val on: Boolean = true) {
    // 1
    UnitType("#unit-type"),
    Pairs("#pairs"),
    Tuples("#tuples"),
    Records("#records"),
    LetBindings("#let-bindings"),
    TypeAscriptions("#type-ascriptions"),
    SumTypes("#sum-types"),
    Lists("#lists"),
    Variants("#variants"),
    FixpointCombinator("#fixpoint-combinator"),

    // 2
    Sequencing("#sequencing"),

    // additional extensions
    // 1
    NaturalLiterals("#natural-literals"),
    NestedFunctionDeclarations("#nested-function-declarations"),
    NullaryFunctions("#nullary-functions"),
    MultiparameterFunctions("#multiparameter-functions"),
    StructuralPatterns("#structural-patterns"),
    NullaryVariantLabels("#nullary-variant-labels"),
    LetrecBindings("#letrec-bindings"),
    LetrecManyBindings("#letrec-many-bindings"),
    LetPatterns("#let-patterns"),
    PatternAscriptions("#pattern-ascriptions");

    companion object {
        fun isSupported(extension: String): Boolean {
            return StellaExtension.entries.any { it.on && it.extensionName == extension }
        }
    }
}