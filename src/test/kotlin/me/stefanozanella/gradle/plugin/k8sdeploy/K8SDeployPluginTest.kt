package me.stefanozanella.gradle.plugin.k8sdeploy

import me.stefanozanella.gradle.plugin.k8sdeploy.support.GradleRunnerTest
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class K8SDeployPluginTest: GradleRunnerTest("gradleTestProject") {
  @Test
  fun `can run task`() {
    val result = runBuildTask("tasks")

    assertEquals(result.tasks.first().outcome, TaskOutcome.SUCCESS)
  }
}
