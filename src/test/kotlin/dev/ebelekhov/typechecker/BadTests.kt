package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaLexer
import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertContains

class BadTests {
    @Test
    fun testBadData() {
        val projectDirAbsolutePath = Paths.get("").toAbsolutePath().toString()
        val resourcesPath = Paths.get(projectDirAbsolutePath, "/src/test/resources/bad")
        val paths = Files
            .walk(resourcesPath)
            .filter { item -> Files.isRegularFile(item) && item.toString().endsWith(".st") }

        paths.forEach {
            try {
                val lexer = stellaLexer(CharStreams.fromString(it.readText()))
                val tokens = CommonTokenStream(lexer)
                val parser = stellaParser(tokens)
                val result = TypeValidator(parser).accept()
                assertContains(
                    (result.exceptionOrNull() as ExitException).error.getMessage(parser),
                    it.parent.name,
                    message = it.pathString)
            }
            catch (exc: Exception) {
                assert(false) { it.pathString }
            }
        }
    }
}
