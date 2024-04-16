package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import dev.ebelekhov.typechecker.types.Type
import org.antlr.v4.runtime.RuleContext

class UnexpectedMemoryAddressError(private val ctx: RuleContext, private val type: Type) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_UNEXPECTED_MEMORY_ADDRESS:
                адрес памяти
                    ${ctx.toStringTree(parser)}
                используется там, где ожидается тип, отличный от типа-ссылки
                    $type
        """.trimIndent()
    }
}
