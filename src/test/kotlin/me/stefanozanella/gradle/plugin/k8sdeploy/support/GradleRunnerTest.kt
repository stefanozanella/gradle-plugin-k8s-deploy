package me.stefanozanella.gradle.plugin.k8sdeploy.support

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.nio.file.Path

private const val BUILD_FILE = "build.gradle.kts"
private const val SETTINGS_FILE = "settings.gradle.kts"

abstract class GradleRunnerTest(private val project: String) {
  @get:Rule
  val tempFolder = TemporaryFolder()

  private val projectDir get() = tempFolder.root
  private val buildFile get() = projectDir.resolve(BUILD_FILE)
  private val settingsFile get() = projectDir.resolve(SETTINGS_FILE)

  @Before
  fun setup() {
    resource(Path.of(project, SETTINGS_FILE).toString()) copyTo settingsFile
    resource(Path.of(project, BUILD_FILE).toString()) copyTo buildFile
  }

  fun runBuildTask(task: String): BuildResult = GradleRunner
    .create()
    .forwardOutput()
    .withPluginClasspath()
    .withArguments(task)
    .withProjectDir(projectDir)
    .build()
}
