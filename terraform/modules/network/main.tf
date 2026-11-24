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

resource "azurerm_subnet" "public" {
  name                 = "snet-betesp-public"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.main.name
  address_prefixes     = ["10.0.1.0/24"]
}

resource "azurerm_subnet" "private" {
  name                 = "snet-betesp-private"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.main.name
  address_prefixes     = ["10.0.2.0/24"]
}


resource "azurerm_network_security_group" "public" {
  name                = "nsg-betesp-public"
  location            = var.location
  resource_group_name = var.resource_group_name

  tags = {
    project    = "BETESP"
    managed_by = "Terraform"
  }
}

resource "azurerm_network_security_group" "private" {
  name                = "nsg-betesp-private"
  location            = var.location
  resource_group_name = var.resource_group_name

  tags = {
    project    = "BETESP"
    managed_by = "Terraform"
  }
}

resource "azurerm_subnet_network_security_group_association" "public" {
  subnet_id                 = azurerm_subnet.public.id
  network_security_group_id = azurerm_network_security_group.public.id
}

resource "azurerm_subnet_network_security_group_association" "private" {
  subnet_id                 = azurerm_subnet.private.id
  network_security_group_id = azurerm_network_security_group.private.id
}

resource "azurerm_network_security_rule" "public_http" {
  name                        = "allow-http"
  priority                    = 100
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "80"
  source_address_prefix       = "Internet"
  destination_address_prefix  = "*"
  resource_group_name         = var.resource_group_name
  network_security_group_name = azurerm_network_security_group.public.name
}

resource "azurerm_network_security_rule" "public_https" {
  name                        = "allow-https"
  priority                    = 110
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "443"
  source_address_prefix       = "Internet"
  destination_address_prefix  = "*"
  resource_group_name         = var.resource_group_name
  network_security_group_name = azurerm_network_security_group.public.name
}


resource "azurerm_route_table" "public" {
  name                = "rt-betesp-public"
  location            = var.location
  resource_group_name = var.resource_group_name

  tags = {
    project    = "BETESP"
    managed_by = "Terraform"
  }
}

resource "azurerm_route_table" "private" {
  name                = "rt-betesp-private"
  location            = var.location
  resource_group_name = var.resource_group_name

  tags = {
    project    = "BETESP"
    managed_by = "Terraform"
  }
}

resource "azurerm_subnet_route_table_association" "public" {
  subnet_id      = azurerm_subnet.public.id
  route_table_id = azurerm_route_table.public.id
}

resource "azurerm_subnet_route_table_association" "private" {
  subnet_id      = azurerm_subnet.private.id
  route_table_id = azurerm_route_table.private.id
}

resource "azurerm_network_security_rule" "public_ssh" {
  name                        = "allow-ssh-admin"
  priority                    = 120
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "22"
  source_address_prefix       = var.admin_source_cidr
  destination_address_prefix  = "*"
  resource_group_name         = var.resource_group_name
  network_security_group_name = azurerm_network_security_group.public.name
}