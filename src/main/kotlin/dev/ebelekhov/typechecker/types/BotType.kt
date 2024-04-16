package dev.ebelekhov.typechecker.types

import org.antlr.v4.runtime.RuleContext

open class BotType : Type {
    override fun toString(): String {
        return "Bot"
    }

    override fun isSubtype(other: Type, ctx: RuleContext): Boolean {
        return true
    }
}
