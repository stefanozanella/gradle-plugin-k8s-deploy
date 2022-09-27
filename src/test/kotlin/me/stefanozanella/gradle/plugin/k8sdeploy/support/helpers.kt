package me.stefanozanella.gradle.plugin.k8sdeploy.support

import java.io.File
import java.net.URL

fun resource(path: String) = object {}.javaClass.classLoader.getResource(path)
  ?: throw Exception("Cannot load resource $path")

infix fun URL.copyTo(destination: File) = File(toURI()).copyTo(destination)
