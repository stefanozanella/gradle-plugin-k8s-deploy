package me.stefanozanella.gradle.plugin.k8sdeploy.tasks

import me.stefanozanella.gradle.plugin.k8sdeploy.extensions.KubernetesDeploymentConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class UpdateK8sDeploymentTask @Inject constructor(
  factory: ObjectFactory,
  config: KubernetesDeploymentConfiguration,
) : DefaultTask() {
  @TaskAction
  fun run() {
    println("Updating K8s deployment")
  }
}
