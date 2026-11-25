resource "azurerm_virtual_network" "main" {
  name                = "vnet-betesp"
  address_space       = var.vnet_address_space
  location            = var.location
  resource_group_name = var.resource_group_name

  tags = {
    project    = "BETESP"
    managed_by = "Terraform"
  }
}

resource "azurerm_subnet" "container_apps" {
  name                 = "snet-betesp-container-apps"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.main.name
  address_prefixes     = ["10.0.0.0/23"]

  delegation {
    name = "container-apps"

    service_delegation {
      name = "Microsoft.App/environments"
      actions = [
        "Microsoft.Network/virtualNetworks/subnets/join/action"
      ]
    }
  }
}

resource "azurerm_subnet" "postgres" {
  name                 = "snet-betesp-postgres"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.main.name
  address_prefixes     = ["10.0.2.0/28"]
}

resource "azurerm_network_security_group" "postgres" {
  name                = "nsg-betesp-postgres"
  location            = var.location
  resource_group_name = var.resource_group_name

  security_rule {
    name                       = "AllowPostgresFromContainerApps"
    priority                   = 100
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "5432"
    source_address_prefix      = "10.0.0.0/23"
    destination_address_prefix = "*"
  }

  tags = {
    project     = "BETESP"
    environment = "development"
    managed_by  = "Terraform"
  }
}

resource "azurerm_subnet_network_security_group_association" "postgres" {
  subnet_id                 = azurerm_subnet.postgres.id
  network_security_group_id = azurerm_network_security_group.postgres.id
}