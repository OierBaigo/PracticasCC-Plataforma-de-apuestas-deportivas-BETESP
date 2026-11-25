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
}

module "database" {
  source = "./modules/database"

  location            = var.location
  resource_group_name = azurerm_resource_group.main.name
  subnet_id           = module.network.postgres_subnet_id

  administrator_login    = var.postgres_admin_login
  administrator_password = var.postgres_admin_password

  depends_on = [module.network]
}

module "container_apps" {
  source = "./modules/container_apps"

  location                 = var.location
  resource_group_name      = azurerm_resource_group.main.name
  infrastructure_subnet_id = module.network.container_apps_subnet_id
  postgres_fqdn            = module.database.private_ip
  postgres_admin_login     = var.postgres_admin_login
  postgres_admin_password  = var.postgres_admin_password
  jwt_secret               = var.jwt_secret
  football_data_api_key    = var.football_data_api_key

  apps = {
    userservice = {
      image = "ghcr.io/oierbaigo/betesp-user-service:latest"
      port  = 8081
    }
    matchservice = {
      image = "ghcr.io/oierbaigo/betesp-match-service:latest"
      port  = 8082
    }
    oddsservice = {
      image = "ghcr.io/oierbaigo/betesp-odds-service:latest"
      port  = 8083
    }
    betsservice = {
      image = "ghcr.io/oierbaigo/betesp-bets-service:latest"
      port  = 8084
    }
    settlementservice = {
      image = "ghcr.io/oierbaigo/betesp-settlement-service:latest"
      port  = 8085
    }
  }
}
