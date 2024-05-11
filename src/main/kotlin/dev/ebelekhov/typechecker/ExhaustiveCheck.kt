package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.*

fun isExhaustiveMatchPattern(expectedType: Type, patterns: List<stellaParser.PatternContext>): Boolean {
    val expandPatterns = patterns.map { expandPattern(it) }

    if (expandPatterns.any { it is stellaParser.PatternVarContext }) return true

    return when (expectedType) {
        BoolType -> checkBoolPattern(expandPatterns)
        UnitType -> checkUnitPattern(expandPatterns)
        NatType -> checkNatPattern(expandPatterns)
        is SumType -> checkSumPattern(expectedType, expandPatterns)
        is VariantType -> checkVariantPattern(expectedType, expandPatterns)
        is TupleType -> checkTuplePattern(expectedType, expandPatterns)
        is RecordType -> checkRecordPattern(expectedType, expandPatterns)
        is ListType -> checkListPattern(expectedType, expandPatterns)
        is AutoType ->
            when(expectedType.constraint) {
                null -> false
                else -> isExhaustiveMatchPattern(expectedType.constraint!!, expandPatterns)
            }
        else -> false
    }
}

private fun expandPattern(pat: stellaParser.PatternContext): stellaParser.PatternContext {
    if (pat is stellaParser.PatternAscContext)
        return expandPattern(pat.pattern_)
    if (pat is stellaParser.ParenthesisedPatternContext)
        return expandPattern(pat.pattern_)
    return pat
}

private fun checkBoolPattern(patterns: List<stellaParser.PatternContext>): Boolean {
    return patterns.any { it is stellaParser.PatternTrueContext } &&
           patterns.any { it is stellaParser.PatternFalseContext }
}

private fun checkUnitPattern(patterns: List<stellaParser.PatternContext>): Boolean {
    return patterns.any { it is stellaParser.PatternUnitContext }
}

private fun checkNatPattern(patterns: List<stellaParser.PatternContext>): Boolean {
    val numbers = patterns
        .filterIsInstance<stellaParser.PatternIntContext>()
        .map { it.n.text.toInt() }
        .toMutableSet()

    var minSuccLength : Int? = null
    patterns
        .filterIsInstance<stellaParser.PatternSuccContext>()
        .map {
            var result = 0
            var pattern: stellaParser.PatternContext = it
            while (pattern is stellaParser.PatternSuccContext) {
                result++
                pattern = pattern.pattern()
            }
            Pair(result, pattern)
        }
        .sortedBy { it.first }
        .forEach {
            if (it.second is stellaParser.PatternVarContext && minSuccLength == null) {
                minSuccLength = it.first
            }
            else if (it.second is stellaParser.PatternIntContext){
                numbers.add(it.first + (it.second as stellaParser.PatternIntContext).n.text.toInt())
            }
        }

    if (minSuccLength == null) return false

    return (0..< minSuccLength!!).all { numbers.contains(it) }
}

private fun checkSumPattern(expectedType: SumType, patterns: List<stellaParser.PatternContext>): Boolean {
    return patterns.any { it is stellaParser.PatternInrContext } &&
           patterns.any { it is stellaParser.PatternInlContext } &&
           isExhaustiveMatchPattern(expectedType.inl, patterns.filterIsInstance<stellaParser.PatternInlContext>().map { it.pattern() }) &&
           isExhaustiveMatchPattern(expectedType.inr, patterns.filterIsInstance<stellaParser.PatternInrContext>().map { it.pattern() })

}

private fun checkVariantPattern(expectedType: VariantType, patterns: List<stellaParser.PatternContext>): Boolean {
    val variantPatterns = patterns.filterIsInstance<stellaParser.PatternVariantContext>()

    return expectedType.variants.all { (name, type) ->
        variantPatterns.any { it.label.text == name } && (
                (type == null) ||
                        isExhaustiveMatchPattern(
                            type,
                            variantPatterns
                                .filter { it.label.text == name }
                                .map { it.pattern() })
                )
    }
}

private fun checkTuplePattern(expectedType: TupleType, patterns: List<stellaParser.PatternContext>): Boolean {
    return expectedType.types.withIndex().all { (idx, type) ->
        isExhaustiveMatchPattern(
            type,
            patterns
                .filterIsInstance<stellaParser.PatternTupleContext>()
                .filter { it.patterns.size == expectedType.types.size }
                .map { p -> p.patterns[idx] }
        )
    }
}

private fun checkRecordPattern(expectedType: RecordType, patterns: List<stellaParser.PatternContext>): Boolean {
    return expectedType.fields.all { field ->
        isExhaustiveMatchPattern(
            field.second,
            patterns
                .filterIsInstance<stellaParser.PatternRecordContext>()
                .filter {
                    it.patterns.size == expectedType.fields.size &&
                            it.patterns.any { lp -> lp.label.text == field.first }
                }
                .map { it.patterns.first { lp -> lp.label.text == field.first }.pattern() })
    }
}

private fun checkListPattern(expectedType: ListType, patterns: List<stellaParser.PatternContext>): Boolean {
    val listPatterns = patterns
        .filterIsInstance<stellaParser.PatternListContext>()
        .map { it.patterns }
        .toMutableList()
    val consWithLastVariable = mutableListOf<MutableList<stellaParser.PatternContext>>()

    patterns
        .filterIsInstance<stellaParser.PatternConsContext>()
        .forEach {
            val nestedPatterns = mutableListOf<stellaParser.PatternContext>()
            var currentPattern : stellaParser.PatternContext = it

            while (currentPattern is stellaParser.PatternConsContext) {
                nestedPatterns.add(currentPattern.head)
                currentPattern = currentPattern.tail
            }

            if (currentPattern is stellaParser.PatternVarContext) {
                consWithLastVariable.add(nestedPatterns)
                listPatterns.add(nestedPatterns)
            }
            else if (currentPattern is stellaParser.PatternListContext) {
                nestedPatterns.addAll(currentPattern.patterns)
                listPatterns.add(nestedPatterns)
            }
        }

    if (consWithLastVariable.isEmpty()) return false

    return consWithLastVariable.any { consPatterns ->
        for (i in 0..consPatterns.size) {
            val patternsWithSize = listPatterns.filter { it.size == i }
            for (j in 0..<i) {
                if (!isExhaustiveMatchPattern(expectedType.type, patternsWithSize.map { it[j] })) {
                    return false
                }
            }
        }

        true
    }
}