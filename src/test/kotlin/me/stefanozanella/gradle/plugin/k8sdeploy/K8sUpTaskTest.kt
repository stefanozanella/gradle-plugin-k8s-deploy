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
import me.stefanozanella.gradle.plugin.k8sdeploy.support.extensions.GradleProject
import me.stefanozanella.gradle.plugin.k8sdeploy.support.resource
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.concurrent.TimeUnit

@DisplayName("k8s-up task")
class K8sUpTaskTest {
  @RegisterExtension
  val build = GradleProject("gradleTestProject")

  @Test
  fun `updates the container image for the selected deployment`() {
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
            RegistryImage.named(ImageReference.of(stack.registryUrl(), "app-to-override", "latest"))
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
          .withName("new-app")
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
              .withName("new-app")
              .withImage("registry:5000/app-to-override:latest")
              .build()
          )
          .endSpec()
          .endTemplate()
          .endSpec()
          .build()
      )
      .create()

    client.apps().deployments().inNamespace("default").withName("new-app").waitUntilReady(30, TimeUnit.SECONDS)


    val result = build.run("k8s-up")

    assertThat(result.tasks.first().outcome).isEqualTo(TaskOutcome.SUCCESS)

    assertThat(
      client
        .apps()
        .deployments()
        .inNamespace("default")
        .withName("new-app")
        .get().spec.template.spec.containers.first().image
    ).isEqualTo("registry:5000/new-app:latest")
  }
}
