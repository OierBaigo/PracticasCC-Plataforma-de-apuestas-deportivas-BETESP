variable "location" {
  description = "Región de Azure donde se desplegará la persistencia"
  type        = string
}

variable "resource_group_name" {
  description = "Grupo de recursos de persistencia"
  type        = string
}

variable "subnet_id" {
  description = "Identificador de la subred para la persistencia"
  type        = string
}