
resource "azurerm_public_ip" "vm" {
  name                = "pip-betesp-vm"
  location            = var.location
  resource_group_name = var.resource_group_name
  allocation_method   = "Static"
  sku                 = "Standard"

  tags = {
    project     = "BETESP"
    environment = "development"
    managed_by  = "Terraform"
  }
}

resource "azurerm_network_interface" "vm" {
  name                = "nic-betesp-vm"
  location            = var.location
  resource_group_name = var.resource_group_name

  ip_configuration {
    name                          = "internal"
    subnet_id                     = var.subnet_id
    private_ip_address_allocation = "Dynamic"
    public_ip_address_id          = azurerm_public_ip.vm.id
  }

  tags = {
    project     = "BETESP"
    environment = "development"
    managed_by  = "Terraform"
  }
}

resource "azurerm_linux_virtual_machine" "main" {
  name                = "vm-betesp"
  computer_name       = "vm-betesp"
  location            = var.location
  resource_group_name = var.resource_group_name
  size                = var.vm_size
  admin_username      = var.admin_username

  network_interface_ids = [
    azurerm_network_interface.vm.id
  ]

  disable_password_authentication = true

  admin_ssh_key {
    username   = var.admin_username
    public_key = var.ssh_public_key
  }

  os_disk {
    name                 = "disk-betesp-os"
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "0001-com-ubuntu-server-jammy"
    sku       = "22_04-lts-gen2"
    version   = "latest"
  }

  tags = {
    project     = "BETESP"
    environment = "development"
    managed_by  = "Terraform"
  }
}
