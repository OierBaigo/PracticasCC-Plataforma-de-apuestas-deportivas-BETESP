variable "location" {
  description = "Región de Azure"
  type        = string
  default     = "belgiumcentral"
}

variable "resource_group_name" {
  description = "Grupo de recursos de BETESP"
  type        = string
  default     = "rg-betesp-infra"
}

variable "vnet_address_space" {
  description = "Rango de direcciones de la red virtual"
  type        = list(string)
  default     = ["10.0.0.0/16"]
}

variable "postgres_admin_login" {
  description = "Usuario administrador de PostgreSQL"
  type        = string
  default     = "betesp"
}

variable "postgres_admin_password" {
  description = "Contraseña del administrador de PostgreSQL"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "Secreto compartido para firmar y validar tokens JWT"
  type        = string
  sensitive   = true
}

variable "football_data_api_key" {
  description = "API key de football-data.org"
  type        = string
  sensitive   = true
}