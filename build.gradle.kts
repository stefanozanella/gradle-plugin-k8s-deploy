@file:Suppress("UnstableApiUsage")

plugins {
  `java-gradle-plugin`
  `kotlin-dsl`
  kotlin("jvm") version "1.6.21"
  id("com.gradle.plugin-publish") version "1.0.0"
}

group = "me.stefanozanella"
version = "0.0.1"

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("me.stefanozanella:kotlin-gradle-little-helpers:0.0.2")
  implementation("com.google.cloud.tools:jib-gradle-plugin:3.3.0")
}

testing {
  suites {
    @Suppress("UNUSED_VARIABLE")
    val test by getting(JvmTestSuite::class) {
      useKotlinTest()
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
