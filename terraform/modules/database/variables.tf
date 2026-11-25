variable "location" {
  description = "Región de Azure"
  type        = string
}

variable "resource_group_name" {
  description = "Resource Group donde se crea la VM"
  type        = string
}

variable "subnet_id" {
  description = "Subred privada para PostgreSQL"
  type        = string
}

variable "administrator_login" {
  description = "Usuario administrador de la VM y PostgreSQL"
  type        = string
}

variable "administrator_password" {
  description = "Contraseña del administrador"
  type        = string
  sensitive   = true
}
