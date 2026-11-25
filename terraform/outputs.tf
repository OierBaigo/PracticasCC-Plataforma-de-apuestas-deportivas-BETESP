output "subscription_id" {
  description = "Identificador de la suscripción de Azure"
  value       = data.azurerm_subscription.current.subscription_id
}

output "subscription_name" {
  description = "Nombre de la suscripción de Azure"
  value       = data.azurerm_subscription.current.display_name
}

output "postgres_private_ip" {
  description = "IP privada de la VM PostgreSQL"
  value       = module.database.private_ip
}

output "container_app_urls" {
  description = "Direcciones internas de los microservicios"
  value       = module.container_apps.app_fqdns
}
