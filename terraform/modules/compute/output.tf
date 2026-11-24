output "compute_id" {
  description = "Identificador de la máquina virtual"
  value       = azurerm_linux_virtual_machine.main.id
}

output "public_ip_address" {
  description = "Dirección IP pública de la máquina virtual"
  value       = azurerm_public_ip.vm.ip_address
}
