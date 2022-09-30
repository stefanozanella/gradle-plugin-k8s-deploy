package me.stefanozanella.gradle.plugin.k8sdeploy.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.nio.file.Files

class K8sAndRegistryCluster : DockerComposeContainer<K8sAndRegistryCluster>(
  File(resource("test-infrastructure/docker-compose.yml").toURI())
) {
  private val outputFolder = Files.createTempDirectory(javaClass.name.toLowerCase())

  var kubeConfigYaml: String = ""
    get() = field
    private set(value) {
      field = value
    }

  init {
    withEnv("K3S_OUTPUT_DIR", outputFolder.toString())
    withExposedService("k3s", 6443)
    withExposedService("registry", 5000)
  }

  override fun start() {
    super.start()

    prepareKubeConfigYaml()
  }

  fun k3sApiUrl() = "https://${getServiceHost("k3s", 6443)}:${getServicePort("k3s", 6443)}"
  fun registryUrl() = "${getServiceHost("registry", 5000)}:${getServicePort("registry", 5000)}"

  private fun prepareKubeConfigYaml() {
    val originalConfig = File(outputFolder.resolve("kubeconfig.yaml").toUri()).readText()

    val exposedServer = k3sApiUrl()

    kubeConfigYaml = replaceServerInKubeConfigYaml(originalConfig, exposedServer)
  }

  private fun replaceServerInKubeConfigYaml(originalConfig: String, newServer: String) =
    with(ObjectMapper(YAMLFactory())) {
      readValue(originalConfig, ObjectNode::class.java).apply {
        (at("/clusters/0/cluster") as ObjectNode).replace(
          "server",
          TextNode(newServer)
        )

        set<TextNode>("current-context", TextNode("default"))
      }.let {
        writerWithDefaultPrettyPrinter().writeValueAsString(it)
      }
    }
}
