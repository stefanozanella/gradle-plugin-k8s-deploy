package me.stefanozanella.gradle.plugin.k8sdeploy

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

data class DockerImageRef @Inject constructor(
  @get:Input val registry: Property<String>,
  @get:Input val image: Property<String>,
  @get:Input val tag: Property<String>
) {
  override fun toString() = "${registryAndImage()}:${tag.get()}"

  private fun registryAndImage() = listOf(registry.get(), image.get())
    .filter(String::isNotEmpty)
    .joinToString("/")
}
