import org.gradle.api.JavaVersion

plugins {
  application
  kotlin("jvm") version "1.7.20"
  id("io.ktor.plugin") version "2.1.1"
  id("me.stefanozanella.gradle.plugin.k8s-deploy") version "0.0.1-SNAPSHOT"
}

group = "com.example"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_18

application {
  mainClass.set("com.example.ApplicationKt")
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("io.ktor:ktor-server-core-jvm:2.1.1")
  implementation("io.ktor:ktor-server-netty-jvm:2.1.1")
}

kubernetesDeployment {
  jvmVersion.set(JavaVersion.VERSION_17)
  registry.set("localhost:5002")
}
