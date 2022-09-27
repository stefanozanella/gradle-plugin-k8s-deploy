package me.stefanozanella.gradle.plugin.k8sdeploy

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import kotlin.test.Test
import kotlin.test.assertEquals

class K8SDeployPluginTest {
  @get:Rule
  val tempFolder = TemporaryFolder()

  private fun getProjectDir() = tempFolder.root
  private fun getBuildFile() = getProjectDir().resolve("build.gradle.kts")
  private fun getSettingsFile() = getProjectDir().resolve("settings.gradle.kts")

  @Test
  fun `can run task`() {
    getSettingsFile().writeText("")
    getBuildFile().writeText(
      """
plugins {
  id("me.stefanozanella.gradle.plugin.k8s-deploy")
}
"""
    )

    val result = GradleRunner.create()
      .forwardOutput()
      .withPluginClasspath()
      .withArguments("tasks")
      .withProjectDir(getProjectDir())
      .build()

    assertEquals(result.tasks.first().outcome, TaskOutcome.SUCCESS)
  }
}
