package me.stefanozanella.gradle.plugin.k8sdeploy

import com.google.cloud.tools.jib.api.Containerizer
import com.google.cloud.tools.jib.api.ImageReference
import com.google.cloud.tools.jib.api.Jib
import com.google.cloud.tools.jib.api.RegistryImage
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath
import com.google.cloud.tools.jib.api.buildplan.Port
import io.fabric8.kubernetes.api.model.ContainerBuilder
import io.fabric8.kubernetes.api.model.LabelSelector
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder
import io.fabric8.kubernetes.client.Config
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import me.stefanozanella.gradle.plugin.k8sdeploy.support.K8sAndRegistryCluster
import me.stefanozanella.gradle.plugin.k8sdeploy.support.asPath
import me.stefanozanella.gradle.plugin.k8sdeploy.support.copyAllTo
import me.stefanozanella.gradle.plugin.k8sdeploy.support.resource
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class K8SDeployPluginTest {
  private val project = "gradleTestProject"

  @TempDir
  private lateinit var tempFolder: Path

  private val sandboxDir get() = tempFolder

  @BeforeEach
  fun setup() {
    resource(project) copyAllTo sandboxDir
  }

  fun runBuildTask(task: String): BuildResult = GradleRunner
    .create()
    .forwardOutput()
    .withPluginClasspath()
    .withArguments(task)
    .withProjectDir(sandboxDir.toFile())
    .build()

  @Test
  fun `builds the sample project successfully`() {
    val stack = K8sAndRegistryCluster().apply { start() }

    Jib
      .from("busybox:1.35")
      .setWorkingDirectory(AbsoluteUnixPath.get("/home/static"))
      .addExposedPort(Port.tcp(8080))
      .addLayer(
        listOf(resource("test-infrastructure/response").asPath()),
        AbsoluteUnixPath.get("/home/static")
      )
      .setEntrypoint(listOf("busybox", "httpd", "-f", "-v", "-p", "8080"))
      .containerize(
        Containerizer
          .to(
            RegistryImage.named(ImageReference.of(stack.registryUrl(), "test-app", "latest"))
          )
          .setAllowInsecureRegistries(true)
      )

    val client = KubernetesClientBuilder().withConfig(Config.fromKubeconfig(stack.kubeConfigYaml)).build()

//    client.apps().deployments().inNamespace("ns").withName("name").rolling().updateImage(mapOf("container" to "image"))

    client
      .apps()
      .deployments()
      .resource(
        DeploymentBuilder()
          .withNewMetadata()
          .withName("test")
          .withNamespace("default")
          .endMetadata()
          .withNewSpec()
          .withReplicas(1)
          .withSelector(
            LabelSelector(emptyList(), mapOf("app" to "test"))
          )
          .withNewTemplate()
          .withNewMetadata()
          .addToLabels("app", "test")
          .endMetadata()
          .withNewSpec()
          .withContainers(
            ContainerBuilder()
              .withName("test")
              .withImage("registry:5000/test-app:latest")
              .build()
          )
          .endSpec()
          .endTemplate()
          .endSpec()
          .build()
      )
      .create()

    client.apps().deployments().inNamespace("default").withName("test").waitUntilReady(30, TimeUnit.SECONDS)

    println(
      client
        .apps()
        .deployments()
        .inNamespace("default")
        .withName("test")
        .get().spec.template.spec.containers.first().image
    )
    val result = runBuildTask("build")

    assertThat(result.tasks.first().outcome).isEqualTo(TaskOutcome.SUCCESS)
  }
}
