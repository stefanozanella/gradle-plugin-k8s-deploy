package me.stefanozanella.gradle.plugin.k8sdeploy

import me.stefanozanella.gradle.plugin.k8sdeploy.tasks.K8sUp
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class K8sDeployPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.tasks.register(K8S_UP_TASK, K8sUp::class.java)
  }
}
