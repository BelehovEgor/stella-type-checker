package dev.ebelekhov.typechecker

enum class StellaExtension(val extensionName: String) {
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
    NaturalLiterals("#natural-literals"),
    NestedFunctionDeclarations("#nested-function-declarations"),
    NullaryFunctions("#nullary-functions"),
    MultiparameterFunctions("#multiparameter-functions"),
    StructuralPatterns("#structural-patterns"),
    NullaryVariantLabels("#nullary-variant-labels"),
    LetrecBindings("#letrec-bindings"),
    LetrecManyBindings("#letrec-many-bindings"),
    LetPatterns("#let-patterns"),
    PatternAscriptions("#pattern-ascriptions"),

    // 2
    Sequencing("#sequencing"),
    References("#references"),
    Panic("#panic"),
    Exceptions("#exceptions"),
    ExceptionTypeAnnotation("#exception-type-annotation"),
    ExceptionTypeDeclaration("#exception-type-declaration"),
    StructuralSubtyping("#structural-subtyping"),
    AmbiguousTypeAsBottom("#ambiguous-type-as-bottom"),
    OpenVariantExceptions("#open-variant-exceptions"),
    TypeCast("#type-cast"),
    TryCastAs("#try-cast-as"),
    TryCastPatterns("#type-cast-patterns"),
    TopType("#top-type"),
    BottomType("#bottom-type"),

    // 3
    ArithmeticOperators("#arithmetic-operators"),
    TypeReconstruction("#type-reconstruction");

    companion object {
        fun fromString(extension: String): StellaExtension {
            return entries.firstOrNull { it.extensionName == extension } ?: error("can't find extension $extension")
        }
    }
}