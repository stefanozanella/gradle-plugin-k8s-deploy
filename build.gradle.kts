@file:Suppress("UnstableApiUsage")

import java.net.URL

plugins {
  `java-gradle-plugin`
  `kotlin-dsl`
  kotlin("jvm") version "1.6.21"
  id("com.gradle.plugin-publish") version "1.1.0"
  id("org.jetbrains.dokka") version "1.8.10"
}

java.sourceCompatibility = JavaVersion.VERSION_18
java.targetCompatibility = JavaVersion.VERSION_1_8

group = "me.stefanozanella"
version = "0.0.1"

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.google.cloud.tools:jib-gradle-plugin:3.3.1")
  implementation("io.fabric8:kubernetes-client:6.4.0")
  implementation("org.bouncycastle:bcprov-jdk15on:1.70")
  implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
  testImplementation("com.google.cloud.tools:jib-core:0.23.0")
  testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
  testImplementation("org.testcontainers:testcontainers:1.17.6")
  testImplementation("org.testcontainers:k3s:1.17.6")
  testImplementation("org.testcontainers:junit-jupiter:1.17.6")
  testImplementation("org.assertj:assertj-core:3.24.2")
}

testing {
  suites {
    @Suppress("UNUSED_VARIABLE")
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}

gradlePlugin {
  @Suppress("UNUSED_VARIABLE")
  val plugin by plugins.creating {
    id = "me.stefanozanella.gradle.plugin.k8s-deploy"
    implementationClass = "me.stefanozanella.gradle.plugin.k8sdeploy.K8sDeployPlugin"
    displayName = "Kubernetes Deployment Gradle Plugin"
    description = "Automatically build and deploy a Docker image for your project to an existing Kubernetes deployment"
  }
}

pluginBundle {
  website = "https://github.com/stefanozanella/gradle-plugin-k8s-deploy"
  vcsUrl = "https://github.com/stefanozanella/gradle-plugin-k8s-deploy.git"
  tags = listOf("kubernetes", "docker", "deployment")
}

tasks.dokkaHtml.configure {
  outputDirectory.set(buildDir.resolve("docs"))

  dokkaSourceSets {
    configureEach {
      val sourcePath = "src/main/kotlin"

      sourceLink {
        localDirectory.set(file(sourcePath))
        remoteUrl.set(URL("https://github.com/stefanozanella/${rootProject.name}/blob/master/$sourcePath"))
        remoteLineSuffix.set("#L")
      }

      jdkVersion.set(java.sourceCompatibility.ordinal)

      includes.from("docs/index.md")
    }
  }
}
