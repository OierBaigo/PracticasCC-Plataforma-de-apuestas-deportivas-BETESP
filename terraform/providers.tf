terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 5.0"
    }
  }
  backend "azurerm" {
    resource_group_name  = "rg-betesp-tfstate"
    storage_account_name = "stbetespstate37826"
    container_name       = "tfstate"
    key                  = "terraform.tfstate"

    use_azuread_auth = true
    use_cli          = true
  }
}

provider "azurerm" {
  features {}
}
