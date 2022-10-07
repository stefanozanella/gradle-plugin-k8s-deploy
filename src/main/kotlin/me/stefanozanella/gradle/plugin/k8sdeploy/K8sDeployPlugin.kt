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

      val dockerImageBuildTask = tasks.named(JibPlugin.BUILD_IMAGE_TASK_NAME, BuildImageTask::class.java)

      val deploymentUpdateTask = tasks.register(
        K8S_UPDATE_DEPLOYMENT_TASK_NAME,
        UpdateK8sDeploymentTask::class.java,
        config
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
            image = listOf(config.registry.get(), config.imageName.get())
              .filter(String::isNotEmpty)
              .joinToString("/")

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
