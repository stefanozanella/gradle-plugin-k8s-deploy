package me.stefanozanella.gradle.plugin.k8sdeploy.extensions

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class KubernetesDeployment @Inject constructor(
  factory: ObjectFactory,
  project: Project,
) {
  val imageName: Property<String> = factory.property(String::class.java)
  val registry: Property<String> = factory.property(String::class.java)
  val baseImage: Property<String> = factory.property(String::class.java)
  val jvmVersion: Property<JavaVersion> = factory.property(JavaVersion::class)

  init {
    imageName.convention(project.name)
    registry.convention("")
    baseImage.convention(jvmVersion.map { "gcr.io/distroless/java$it-debian11" })
    jvmVersion.convention(project.extensions.getByType(JavaPluginExtension::class.java).sourceCompatibility)
  }
}
