variable "location" {
  description = "Región de Azure donde se desplegará la red"
  type        = string
}

variable "resource_group_name" {
  description = "Grupo de recursos donde se desplegará la red"
  type        = string
}

variable "vnet_address_space" {
  description = "Rango de direcciones IP de la red virtual"
  type        = list(string)
}