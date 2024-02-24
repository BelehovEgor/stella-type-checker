package dev.ebelekhov.typechecker

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.readText
import kotlin.test.Test

class OkTests {
    @Test
    fun testOkData() {
        val projectDirAbsolutePath = Paths.get("").toAbsolutePath().toString()
        val resourcesPath = Paths.get(projectDirAbsolutePath, "/src/test/resources/ok")
        val paths = Files
            .walk(resourcesPath)
            .filter { item -> Files.isRegularFile(item) && item.toString().endsWith(".st") }

        paths.forEach {
            TypeValidator(StellaVisitor()).accept(it.readText())
        }
    }
}