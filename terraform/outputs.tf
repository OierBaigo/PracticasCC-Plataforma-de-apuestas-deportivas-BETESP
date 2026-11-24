output "subscription_id" {
  description = "Identificador de la suscripción de Azure"
  value       = data.azurerm_subscription.current.subscription_id
}

output "subscription_name" {
  description = "Nombre de la suscripción de Azure"
  value       = data.azurerm_subscription.current.display_name
}

output "vm_public_ip" {
  description = "IP pública de la máquina virtual BETESP"
  value       = module.compute.public_ip_address
}