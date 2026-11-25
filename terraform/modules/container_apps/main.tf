
locals {
  app_environment = {
    userservice = [
      {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${var.postgres_fqdn}:5432/betesp_users?sslmode=disable"
      }
    ]

    matchservice = [
      {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${var.postgres_fqdn}:5432/betesp_matches?sslmode=disable"
      },
      {
        name  = "SETTLEMENT_SERVICE_BASE_URL"
        value = "http://ca-settlementservice.${azurerm_container_app_environment.main.default_domain}"
      }
    ]

    oddsservice = [
      {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${var.postgres_fqdn}:5432/betesp_odds?sslmode=disable"
      },
      {
        name  = "MATCH_SERVICE_BASE_URL"
        value = "http://ca-matchservice.${azurerm_container_app_environment.main.default_domain}"
      }
    ]

    betsservice = [
      {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${var.postgres_fqdn}:5432/betesp_bets?sslmode=disable"
      },
      {
        name  = "MATCH_SERVICE_BASE_URL"
        value = "http://ca-matchservice.${azurerm_container_app_environment.main.default_domain}"
      },
      {
        name  = "ODDS_SERVICE_BASE_URL"
        value = "http://ca-oddsservice.${azurerm_container_app_environment.main.default_domain}"
      },
      {
        name  = "USER_SERVICE_BASE_URL"
        value = "http://ca-userservice.${azurerm_container_app_environment.main.default_domain}"
      }
    ]

    settlementservice = [
      {
        name  = "BET_SERVICE_BASE_URL"
        value = "http://ca-betsservice.${azurerm_container_app_environment.main.default_domain}"
      },
      {
        name  = "USER_SERVICE_BASE_URL"
        value = "http://ca-userservice.${azurerm_container_app_environment.main.default_domain}"
      }
    ]
  }
}

resource "azurerm_container_app_environment" "main" {
  name                           = "cae-betesp-dev"
  location                       = var.location
  resource_group_name            = var.resource_group_name
  infrastructure_subnet_id       = var.infrastructure_subnet_id
  internal_load_balancer_enabled = false
  logs_destination               = "azure-monitor"
  workload_profile {
    name                  = "Consumption"
    workload_profile_type = "Consumption"
    minimum_count         = 0
    maximum_count         = 0
  }

  tags = {
    project     = "BETESP"
    environment = "development"
    managed_by  = "Terraform"
  }
}

resource "azurerm_container_app" "apps" {
  for_each = var.apps

  name                         = "ca-${each.key}"
  container_app_environment_id = azurerm_container_app_environment.main.id
  resource_group_name          = var.resource_group_name
  revision_mode                = "Single"
  workload_profile_name        = "Consumption"

  secret {
    name  = "postgres-password"
    value = var.postgres_admin_password
  }

  secret {
    name  = "jwt-secret"
    value = var.jwt_secret
  }

  secret {
    name  = "football-data-api-key"
    value = var.football_data_api_key
  }

  template {
    min_replicas = 0
    max_replicas = 1

    container {
      name   = each.key
      image  = each.value.image
      cpu    = 0.25
      memory = "0.5Gi"

      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = var.postgres_admin_login
      }

      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "postgres-password"
      }

      env {
        name        = "JWT_SECRET"
        secret_name = "jwt-secret"
      }

      dynamic "env" {
        for_each = local.app_environment[each.key]

        content {
          name  = env.value.name
          value = env.value.value
        }
      }

      dynamic "env" {
        for_each = each.key == "matchservice" ? [1] : []

        content {
          name        = "FOOTBALL_DATA_API_KEY"
          secret_name = "football-data-api-key"
        }
      }
    }
  }

  ingress {
    external_enabled = false
    target_port      = each.value.port
    transport        = "http"

    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  tags = {
    project     = "BETESP"
    environment = "development"
    managed_by  = "Terraform"
  }
}
