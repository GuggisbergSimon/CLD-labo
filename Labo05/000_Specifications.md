# Lab 05 : Kubernetes

- Work in a group of 2 students.
- Duration of this lab is 4 periods.

#### Pedagogical objectives

  - Become familiar with Kubernetes and its command-line tool `kubectl`
    
  - Define, deploy, run and scale a web-application using the Kubernetes abstractions of Pods, Services and Deployments
    
  - Use a local cluster for a test deployment before deploying on a public cloud service
    
  - Verify elasticity and resilience of a Kubernetes-managed application

## Introduction and prerequisites

In this lab you will perform a number of tasks and document your progress in a lab report. Each task specifies one or more deliverables to be produced.  Collect all the deliverables in your lab report. Give the lab report a structure that mimics the structure of this document.

You will first install a Kubernetes test cluster on your local machine using the `minikube` tool. You manage this cluster through the `kubectl` command line tool. Then you will deploy a provided "To Do" reminder application that uses a Redis key-value store as example. The goal of the first part is to deploy a complete version of a three-tier application (Frontend + API Server + Redis) using Pods.

In the second part of the lab you will deploy the same application on a public cloud service for running Kubernetes containers: Google Kubernetes Engine.

The third part of the lab will require you to make the application resilient to failures. You will deploy multiple Pods with a Deployment for the Frontend and API services.

The following resources and tools are required for this laboratory session:

  * Any modern web browser
  * The Minikube command line tool (instructions to install below in Task 1)
  * The Kubernetes command line tool `kubectl` (instructions to install below in Task 1)
  * A Google Cloud platform account

#### Tasks

In this lab you will perform a number of tasks and document your
progress in a lab report. Each task specifies one or more deliverables
to be produced. Collect all the deliverables in your lab report. Give
the lab report a structure that mimics the structure of this document.

## Additional Documentation

Kubernetes documentation can be found on the following pages:

  * User Guide explaining the main concepts: <https://kubernetes.io/docs/user-guide/>
  * What is a Pod: <https://kubernetes.io/docs/concepts/workloads/pods/pod/>
  * What is a Service: <https://kubernetes.io/docs/concepts/services-networking/service/>
  * What is a Deployment: <https://kubernetes.io/docs/concepts/workloads/controllers/deployment/>
  * HowTo define environment variables: <https://kubernetes.io/docs/tasks/configure-pod-container/define-environment-variable-container/>
  * How-To's for do typical tasks in Kubernetes: https://kubernetes.io/docs/tasks/
  * Interactive `kubectl` command reference: <https://kubernetes.io/docs/reference/generated/kubectl/kubectl-commands>
  * How to fix failed get resource metric in Kubernetes HPA: <https://aptakube.com/blog/how-to-fix-failedgeteesourcemetric-hpa>

> [!TIP]
>
> ## Troubleshooting
>
> ### Display the log of a Pod
>
> See the log of a Pod [with streaming enabled]:
>
> ```sh
> $ kubectl logs [-f] pod_name
> ```
>
> ### Run a shell session inside a container
>
> Run an interactive shell command in a Pod:
>
> ```sh
> $ kubectl exec -it pod_name -- sh
> ```
>
> Then you can test if environment variables are set with `env`, if web apps are reachable with `curl`, etc.

### TODO

* [Task 001 - Deploy the application on a local test cluster](001_DeployAppLocalTestCluster.md)
* [Task 002 - Deploy the application in Kubernetes Engine](002_DeployAppInKubEngine.md)
* [Task 003 - Add and exercise resilience](003_Resilience.md)
* [Task 004 - Clean Up](004_CleanUp.md)
