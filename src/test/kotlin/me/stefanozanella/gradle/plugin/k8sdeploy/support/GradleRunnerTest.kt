package me.stefanozanella.gradle.plugin.k8sdeploy.support

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder

abstract class GradleRunnerTest(private val project: String) {
  @get:Rule
  val tempFolder = TemporaryFolder()

  private val sandboxDir get() = tempFolder.root

  @Before
  fun setup() {
    resource(project) copyAllTo sandboxDir
  }

  fun runBuildTask(task: String): BuildResult = GradleRunner
    .create()
    .forwardOutput()
    .withPluginClasspath()
    .withArguments(task)
    .withProjectDir(sandboxDir)
    .build()
}
