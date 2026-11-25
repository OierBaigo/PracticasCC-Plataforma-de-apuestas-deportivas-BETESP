variable "location" {
  type = string
}

variable "resource_group_name" {
  type = string
}

variable "infrastructure_subnet_id" {
  type = string
}

variable "apps" {
  type = map(object({
    image = string
    port  = number
  }))
}

variable "postgres_fqdn" {
  type = string
}

variable "postgres_admin_login" {
  type = string
}

variable "postgres_admin_password" {
  type      = string
  sensitive = true
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
