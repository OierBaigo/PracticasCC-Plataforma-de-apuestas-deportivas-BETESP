
variable "location" {
  description = "Región de Azure donde se desplegará la máquina virtual"
  type        = string
}

variable "resource_group_name" {
  description = "Grupo de recursos de la infraestructura BETESP"
  type        = string
}

variable "subnet_id" {
  description = "Identificador de la subred donde se desplegará la máquina virtual"
  type        = string
}

variable "vm_size" {
  description = "Tamaño de la máquina virtual"
  type        = string
  default     = "Standard_B1s"
}

variable "admin_username" {
  description = "Usuario administrador de Linux"
  type        = string
  default     = "betespadmin"
}

variable "ssh_public_key" {
  description = "Clave pública SSH para acceder a la máquina virtual"
  type        = string
  sensitive   = true
}
