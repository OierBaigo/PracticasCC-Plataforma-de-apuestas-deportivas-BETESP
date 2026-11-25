resource "azurerm_network_interface" "postgres" {
  name                = "nic-betesp-postgres"
  location            = var.location
  resource_group_name = var.resource_group_name

  ip_configuration {
    name                          = "internal"
    subnet_id                     = var.subnet_id
    private_ip_address_allocation = "Dynamic"
  }

  tags = {
    project     = "BETESP"
    environment = "development"
    managed_by  = "Terraform"
  }
}

resource "azurerm_linux_virtual_machine" "postgres" {
  name                = "vm-betesp-postgres"
  computer_name       = "betesp-postgres"
  resource_group_name = var.resource_group_name
  location            = var.location

  # SKU incluida en Azure for Students
  # No especificamos zone: las zonas 2 y 3 están restringidas.
  size = "Standard_B2ats_v2"

  admin_username                  = var.administrator_login
  admin_password                  = var.administrator_password
  disable_password_authentication = false

  custom_data = base64encode(templatefile("${path.module}/cloud-init.yaml.tftpl", {
    administrator_login        = var.administrator_login
    administrator_password_b64 = base64encode(var.administrator_password)
  }))

  network_interface_ids = [
    azurerm_network_interface.postgres.id
  ]

  os_disk {
    name                 = "osdisk-betesp-postgres"
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
    disk_size_gb         = 30
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "0001-com-ubuntu-server-jammy"
    sku       = "22_04-lts"
    version   = "latest"
  }

  tags = {
    project     = "BETESP"
    environment = "development"
    managed_by  = "Terraform"
  }
  lifecycle {
    ignore_changes = [
      custom_data
    ]
  }
}
