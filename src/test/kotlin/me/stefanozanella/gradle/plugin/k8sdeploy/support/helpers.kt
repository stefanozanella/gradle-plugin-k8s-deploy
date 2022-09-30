package me.stefanozanella.gradle.plugin.k8sdeploy.support

import java.net.URL
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

fun resource(path: String) = object {}.javaClass.classLoader.getResource(path)
  ?: throw Exception("Cannot load resource $path")

fun URL.asPath() = Path.of(toURI())

infix fun URL.copyAllTo(destination: Path) {
  if (!Files.isDirectory(destination))
    throw Exception("Cannot copy list of files into a file, please specify a directory")

  val source = asPath()

  Files.walkFileTree(source, object : SimpleFileVisitor<Path>() {
    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult {
      val target = destination.resolve(source.relativize(dir))

      try {
        Files.copy(dir, target)
      } catch (e: FileAlreadyExistsException) {
        if (!Files.isDirectory(target)) throw e
      }

      return FileVisitResult.CONTINUE
    }

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
      Files.copy(file, destination.resolve(source.relativize(file)))
      return FileVisitResult.CONTINUE
    }
  })
}
