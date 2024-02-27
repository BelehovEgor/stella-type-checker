package dev.ebelekhov.typechecker

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.name
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
                val result = TypeValidator(StellaVisitor()).accept(it.readText())
                assertContains(
                    (result.exceptionOrNull() as ExitException).error.getMessage(),
                    it.parent.name,
                    message = it.toString())
            }
            catch (exc: NotImplementedError) {
                assert(false) { it.fileName }
            }
        }
    }
}
