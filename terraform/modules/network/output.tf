output "vnet_id" {
  description = "Identificador de la red virtual"
  value       = azurerm_virtual_network.main.id
}

output "subnet_ids" {
  description = "Identificadores de las subredes"
  value = {
    public  = azurerm_subnet.public.id
    private = azurerm_subnet.private.id
  }
}