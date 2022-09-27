@file:Suppress("UnstableApiUsage")

plugins {
  `java-gradle-plugin`
  `kotlin-dsl`
  kotlin("jvm") version "1.6.21"
}

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
  }
}
