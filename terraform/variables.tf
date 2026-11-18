variable "location" {
  description = "Región de Azure para la infraestructura BETESP"
  type        = string
  default     = "italynorth"
}

variable "resource_group_name" {
  description = "Grupo de recursos de la infraestructura BETESP"
  type        = string
  default     = "rg-betesp-infra"
}

variable "vnet_address_space" {
  description = "Rango de direcciones IP de la red virtual"
  type        = list(string)
  default     = ["10.0.0.0/16"]
}