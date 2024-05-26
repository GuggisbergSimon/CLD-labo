variable "gcp_project_id" {
  description = "Google Cloud Project ID"
  type        = string
  nullable    = false
}

variable "gcp_service_account_key_file_path" {
  description = "Path to the service account key file"
  type        = string
  nullable    = false
}

variable "gce_instance_name" {
  description = "Google Compute Engine instance name"
  type        = string
  nullable    = false
}

variable "gce_instance_user" {
  description = "Google Compute Engine instance user"
  type        = string
  nullable    = false
}

variable "gce_ssh_pub_key_file_path" {
  description = "Path to the SSH public key file"
  type        = string
  nullable    = false
}
