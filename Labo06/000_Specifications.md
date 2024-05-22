#### Organization

* Work in a group of 2 students.
* Duration of this lab is 2 periods.

#### Pedagogical objectives

* Deploy a web site in an automated fashion

* Become familiar with tools for cloud deployment and configuration management

* Apply the principles of Infrastructure-as-Code and  Desired State Configuration

* (Optional) Use a simple CI/CD pipeline to manage your cloud infrastructure 

#### Tasks

In this lab you will perform a number of tasks and document your
progress in a lab report. Each task specifies one or more deliverables
to be produced.  Collect all the deliverables in your lab report. Give
the lab report a structure that mimics the structure of this document.

#### Overview

In this lab you will deploy a web site running on a virtual machine in the cloud using tools that adhere to the principles of Infrastructure-as-Code and Desired State Configuration. The web server is NGINX and the cloud is Google Cloud.

* Infrastructure-as-Code: instead of using a web console to create resources manually by clicking buttons you will describe the infrastructure you want to create in a file as code. You put this file in a Version Control System like git and you treat it as other code files.
* Desired State Configuration: instead of saying "first create this, then modify that" imperatively, you will describe the desired state declaratively. A tool will determine the actual state, compare it to the desired state, and figure out by itself what needs to be done to transform the actual state into the desired state.

In the first part we provision the cloud infrastructure, i.e., we create the cloud resources (virtual machine) needed for our application. The tool for cloud provisioning is Terraform.

In the second part we configure the virtual machine by installing a web server and its configuration files. The tool for configuration management is Ansible.