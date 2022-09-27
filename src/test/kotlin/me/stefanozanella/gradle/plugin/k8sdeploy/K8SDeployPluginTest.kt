package me.stefanozanella.gradle.plugin.k8sdeploy

import me.stefanozanella.gradle.plugin.k8sdeploy.support.GradleRunnerTest
import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertEquals

class K8SDeployPluginTest : GradleRunnerTest("gradleTestProject") {
  @Test
  fun `builds the sample project successfully`() {
    val result = runBuildTask("build")

    assertEquals(result.tasks.first().outcome, TaskOutcome.SUCCESS)
  }
}
