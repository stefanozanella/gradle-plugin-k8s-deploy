package me.stefanozanella.gradle.plugin.k8sdeploy.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class K8sUp : DefaultTask() {
  @TaskAction
  fun run() {}
}
