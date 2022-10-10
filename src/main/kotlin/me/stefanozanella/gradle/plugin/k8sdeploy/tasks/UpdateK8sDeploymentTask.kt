package me.stefanozanella.gradle.plugin.k8sdeploy.tasks

import io.fabric8.kubernetes.client.KubernetesClientBuilder
import me.stefanozanella.gradle.plugin.k8sdeploy.DockerImageRef
import me.stefanozanella.gradle.plugin.k8sdeploy.extensions.KubernetesDeploymentConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class UpdateK8sDeploymentTask @Inject constructor(
  private val config: KubernetesDeploymentConfiguration,
  @Nested val targetImage: DockerImageRef,
) : DefaultTask() {
  @TaskAction
  fun run() {
    val k8s = KubernetesClientBuilder().build()

    val deployment = k8s
      .apps()
      .deployments()
      .inNamespace(config.deploymentNamespace.get())
      .withName(config.deploymentName.get())

    deployment.rolling().updateImage(mapOf(config.podName.get() to targetImage.toString()))
    deployment.waitUntilReady(30, TimeUnit.SECONDS)
  }
}
