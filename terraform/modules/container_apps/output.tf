output "app_fqdns" {
  description = "FQDN interno de cada microservicio"
  value = {
    for name, app in azurerm_container_app.apps :
    name => app.ingress[0].fqdn
  }
}

output "environment_id" {
  value = azurerm_container_app_environment.main.id
}
