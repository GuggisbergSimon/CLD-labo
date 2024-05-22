# Task 1: Install Terraform

In this task you will [install Terraform](https://learn.hashicorp.com/tutorials/terraform/install-cli) on your local machine.

The installation procedure depends on the OS on your local machine. If you run Windows it is recommended you use a Linux installation to run Terraform (Windows Subsystem for Linux with Debian/Ubuntu is fine).

To install on Linux: Use Debian/Ubuntu's package manager `apt`:

# Linux Distribution

## Install the required packages to add Terraform APT repository

```bash
sudo apt-get update && sudo apt-get install -y gnupg software-properties-common curl
```

## Add the HashiCorp GPG key.
```bash
curl -fsSL https://apt.releases.hashicorp.com/gpg | sudo apt-key add -
```
    
## Add the official HashiCorp Linux repository.
```bash
sudo apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main"
```

## Update to add the repository, and install the Terraform CLI.
```bash
sudo apt-get update && sudo apt-get install terraform
```

# Mac OS

To install on macOS: Use the Homebrew package manager `brew`:

## Install the HashiCorp tap
```bash
brew tap hashicorp/tap
```

## Install Terraform
```bash
brew install hashicorp/tap/terraform
```

* Verify that Terraform is installed correctly by running:

```bash
terraform --version
```

You should see output similar to the following:

```bash
Terraform v1.1.9 on darwin_amd64
```