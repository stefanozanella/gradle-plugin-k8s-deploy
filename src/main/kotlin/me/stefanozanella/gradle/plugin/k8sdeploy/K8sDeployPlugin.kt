package me.stefanozanella.gradle.plugin.k8sdeploy

import com.google.cloud.tools.jib.gradle.BuildImageTask
import com.google.cloud.tools.jib.gradle.JibExtension
import com.google.cloud.tools.jib.gradle.JibPlugin
import me.stefanozanella.gradle.plugin.k8sdeploy.extensions.KubernetesDeploymentConfiguration
import me.stefanozanella.gradle.plugin.k8sdeploy.tasks.UpdateK8sDeploymentTask
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class K8sDeployPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    with(project) {
      val config =
        extensions.create(KUBERNETES_DEPLOYMENT_EXTENSION_NAME, KubernetesDeploymentConfiguration::class.java)

      val pushDockerImage = DockerImageRef(
        config.imageRegistry,
        config.imageName,
        config.imageTag,
      )

      val pullDockerImage = pushDockerImage.copy(registry = config.k8sRegistry)

      val dockerImageBuildTask = tasks.named(JibPlugin.BUILD_IMAGE_TASK_NAME, BuildImageTask::class.java)

      val deploymentUpdateTask = tasks.register(
        K8S_UPDATE_DEPLOYMENT_TASK_NAME,
        UpdateK8sDeploymentTask::class.java,
        config,
        pullDockerImage,
      ).apply {
        configure {
          mustRunAfter(dockerImageBuildTask)
        }
      }

      tasks.register(K8S_UP_TASK_NAME) {
        dependsOn(
          deploymentUpdateTask,
          dockerImageBuildTask,
        )
      }

      afterEvaluate {
        extensions.getByType(JibExtension::class.java).apply {
          to {
            image = pushDockerImage.toString()

            setAllowInsecureRegistries(true)
          }

          from {
            image = config.baseImage.get()
          }
        }
      }
    }
  }
}
