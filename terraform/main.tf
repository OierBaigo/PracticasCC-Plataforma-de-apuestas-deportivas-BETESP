data "azurerm_subscription" "current" {}

module "network" {
  source = "./modules/network"

  location            = var.location
  resource_group_name = var.resource_group_name
  vnet_address_space  = var.vnet_address_space
}

module "compute" {
  source = "./modules/compute"

  location            = var.location
  resource_group_name = var.resource_group_name
  subnet_id            = try(module.network.subnet_ids["compute"], null)
}

module "persistence" {
  source = "./modules/persistence"

  location            = var.location
  resource_group_name = var.resource_group_name
  subnet_id            = try(module.network.subnet_ids["persistence"], null)
}