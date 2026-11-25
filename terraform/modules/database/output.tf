output "private_ip" {
  description = "IP privada de la VM PostgreSQL"
  value       = azurerm_network_interface.postgres.private_ip_address
}

output "vm_id" {
  description = "Identificador de la VM PostgreSQL"
  value       = azurerm_linux_virtual_machine.postgres.id
}

