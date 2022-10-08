# Kubernetes Deploy Gradle Plugin

A Gradle plugin that automatically builds and deploy a Docker image for your project to an existing Kubernetes
deployment.

## How does it work

This plugin exposes a task you can use to get your project packaged into a Docker image and get that same image
deployed into a Kubernetes cluster. This is achieved using [Jib](https://github.com/GoogleContainerTools/jib) to
efficiently pack your code into a container image built directly onto the cluster registry, and by triggering a rollout
via an image update of your Kubernetes deployment.

## Minikube Quick Start

If you want to hit the ground running locally with a [Minikube](http://minikube.sigs.k8s.io/) cluster, all you need to
do is the following:

* create a new Gradle project, and configure the plugin in your `build.gradle.kts` file

```kotlin
plugins {
  id("me.stefanozanella.k8s-deploy")
}

// ...

kubernetesDeployment {
  deploymentName.set("kubedeploy")
  podName.set("kubedeploy")
  baseImageName.set("kubedeploy")
  pushDockerImage.set(false)
}
```

* start Minikube and configure a new Kubernetes deployment with an `imagePullPolicy` of `Never`, named after the name
  you specified in the plugin
* prepare your environment to use Minikube's Docker daemon

```shell
eval $(minikube docker-env)
```

* continuously deploy your project to Minikube

```shell
./gradlew -t k8sUp
```

## Pre-requisites

This plugin doesn't assume anything about your infrastructure setup, it only needs
a [deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)
to be available in a Kubernetes cluster reachable locally using e.g. `kubectl`. You're free to decide how that
deployment is configured (for example, you'll decide on your own rollout strategy) and where the Kubernetes cluster is
located. The environment in which you'll run the plugin's tasks also need to point to the cluster's Docker daemon,
since the plugin will build your project's image directly onto the cluster's registry to avoid unnecessary complexity.

To know if you're ready to roll, you should see an output in your terminal similar to the following:

```shell
$ kubectl get deployment
NAME         READY   UP-TO-DATE   AVAILABLE   AGE
k8sdeploy    1/1     1            1           10d

$ env | grep DOCKER_HOST
DOCKER_HOST=tcp://127.0.0.1:50193
```

## Configuring the plugin

The plugin exposes a `kubernetesDeployment` extension with the following properties:

* `imageName`: base name of the resulting Docker image that will get deployed. Defaults to `project.name`
* `imageRegistry`: registry that should host the resulting Docker image. The default is empty.
* `imageTag`: what tag to apply to the resulting Docker image. Defaults to a dynamic "snapshot string" in the form
  `dev-snapshot-<date timestamp>`.
* `baseImage`: the Docker base image to build the application upon. Defaults to the Java [Distroless](https://github.
  com/GoogleContainerTools/distroless) image corresponding to the specified `jvmVersion`.
* `jvmVersion`: used to determine the version of the base image used to build your container. The plugin
  uses [Distroless](https://github.com/GoogleContainerTools/distroless) so the version needs to match one of the
  [available Java base images](https://github.com/GoogleContainerTools/distroless/tree/main/java). Defaults to
  `project.java.sourceCompatibility`.
* `deploymentName`: name of the deployment to update during rollout. Defaults to `project.name`.
* `deploymentNamespace`: namespace where to find the deployment. Defaults to `default`.
* `podName`: name of the pod to update during rollout. Defaults to `project.name`.
* `k8sRegistry`: registry from where Kubernetes should pull the application's Docker image. This is available for 
  the case where, for example, a registry is run within the Kubernetes infrastructure and is accessible via internal 
  DNS by the control plane, but is available under a different hostname for the client pushing the image. Defaults 
  to `imageRegistry`. NOTE: In the majority of cases you don't need to set this, and setting `imageRegistry` will 
  automatically make Kubernetes pull from the same registry.
