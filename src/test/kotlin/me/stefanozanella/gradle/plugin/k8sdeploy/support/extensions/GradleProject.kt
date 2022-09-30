package me.stefanozanella.gradle.plugin.k8sdeploy.support.extensions

import me.stefanozanella.gradle.plugin.k8sdeploy.support.copyAllTo
import me.stefanozanella.gradle.plugin.k8sdeploy.support.resource
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.nio.file.Files

class GradleProject(private val project: String) : BeforeEachCallback {
  private var sandboxDir = Files.createTempDirectory("${javaClass.name}--$project")

  private val runner = GradleRunner.create()
    .forwardOutput()
    .withPluginClasspath()
    .withProjectDir(sandboxDir.toFile())

  override fun beforeEach(context: ExtensionContext) {
    resource(project) copyAllTo sandboxDir
  }

  fun run(vararg arguments: String): BuildResult = runner.withArguments(*arguments).build()
}
