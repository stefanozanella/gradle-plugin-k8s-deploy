package me.stefanozanella.gradle.plugin.k8sdeploy

import com.google.cloud.tools.jib.gradle.BuildImageTask
import com.google.cloud.tools.jib.gradle.JibExtension
import com.google.cloud.tools.jib.gradle.JibPlugin
import me.stefanozanella.gradle.plugin.k8sdeploy.extensions.KubernetesDeployment
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class K8sDeployPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    with(project) {
      val config = extensions.create(KUBERNETES_DEPLOYMENT_EXTENSION_NAME, KubernetesDeployment::class.java)

      val dockerImageBuildTask = tasks.named(JibPlugin.BUILD_IMAGE_TASK_NAME, BuildImageTask::class.java)

      afterEvaluate {
        extensions.getByType(JibExtension::class.java).apply {
          to {
            image = listOf(config.registry.get(), config.imageName.get())
              .filter(String::isNotEmpty)
              .joinToString("/")
          }

          from {
            image = config.baseImage.get()
          }
        }
      }

      tasks.register(K8S_UP_TASK_NAME) {
        dependsOn(dockerImageBuildTask)
      }
    }
  }
}
