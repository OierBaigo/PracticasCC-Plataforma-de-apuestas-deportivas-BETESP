data "azurerm_subscription" "current" {}

resource "azurerm_resource_group" "main" {
  name     = var.resource_group_name
  location = var.location

  tags = {
    project     = "BETESP"
    environment = "development"
    managed_by  = "Terraform"
  }
}

module "network" {
  source = "./modules/network"

  location            = var.location
  resource_group_name = azurerm_resource_group.main.name
  vnet_address_space  = var.vnet_address_space
  admin_source_cidr   = var.admin_source_cidr
}

module "compute" {
  source = "./modules/compute"

  location            = var.location
  resource_group_name = azurerm_resource_group.main.name
  subnet_id           = module.network.subnet_ids["public"]
  ssh_public_key      = var.ssh_public_key
}

module "persistence" {
  source = "./modules/persistence"

  location            = var.location
  resource_group_name = azurerm_resource_group.main.name
  subnet_id           = module.network.subnet_ids["private"]
}