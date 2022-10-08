package me.stefanozanella.gradle.plugin.k8sdeploy.extensions

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

open class KubernetesDeploymentConfiguration @Inject constructor(
  factory: ObjectFactory,
  project: Project,
) {
  val imageName: Property<String> = factory.property(String::class.java)
  val imageRegistry: Property<String> = factory.property(String::class.java)
  val imageTag: Property<String> = factory.property(String::class.java)
  val baseImage: Property<String> = factory.property(String::class.java)
  val jvmVersion: Property<JavaVersion> = factory.property(JavaVersion::class)
  val deploymentName: Property<String> = factory.property(String::class.java)
  val deploymentNamespace: Property<String> = factory.property(String::class.java)
  val podName: Property<String> = factory.property(String::class.java)
  val k8sRegistry: Property<String> = factory.property(String::class.java)
  val pushDockerImage: Property<Boolean> = factory.property(Boolean::class.java)

  init {
    imageName.convention(project.name)
    imageTag.convention(
      "dev-snapshot-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}"
    )
    imageRegistry.convention("")
    baseImage.convention(jvmVersion.map { "gcr.io/distroless/java$it-debian11" })
    jvmVersion.convention(project.extensions.getByType(JavaPluginExtension::class.java).sourceCompatibility)
    deploymentName.convention(project.name)
    deploymentNamespace.convention("default")
    podName.convention(project.name)
    k8sRegistry.convention(imageRegistry)
    pushDockerImage.convention(true)
  }
}
