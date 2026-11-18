output "subscription_id" {
  description = "Identificador de la suscripción de Azure"
  value       = data.azurerm_subscription.current.subscription_id
}

output "subscription_name" {
  description = "Nombre de la suscripción de Azure"
  value       = data.azurerm_subscription.current.display_name
}
