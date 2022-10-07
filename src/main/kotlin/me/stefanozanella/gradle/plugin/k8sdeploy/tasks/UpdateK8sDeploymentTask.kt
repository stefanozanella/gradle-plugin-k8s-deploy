package me.stefanozanella.gradle.plugin.k8sdeploy.tasks

import io.fabric8.kubernetes.client.KubernetesClientBuilder
import me.stefanozanella.gradle.plugin.k8sdeploy.DockerImageRef
import me.stefanozanella.gradle.plugin.k8sdeploy.extensions.KubernetesDeploymentConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class UpdateK8sDeploymentTask @Inject constructor(
  private val config: KubernetesDeploymentConfiguration,
  @Nested val targetImage: DockerImageRef,
) : DefaultTask() {
  @TaskAction
  fun run() {
    val client = KubernetesClientBuilder().build() //.withConfig(Config.fromKubeconfig(stack.kubeConfigYaml)).build()

    client
      .apps()
      .deployments()
      .inNamespace(config.deploymentNamespace.get())
      .withName(config.deploymentName.get())
      .rolling()
      .updateImage(mapOf(config.podName.get() to targetImage.toString()))

//    println(
//      "Updating K8s pod ${config.podName.get()} for deployment ${config.deploymentName.get()} in namespace ${
//        config
//          .deploymentNamespace.get()
//      } with " +
//          "image $targetImage"
//    )
  }
}
