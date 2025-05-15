# global_outputs.tf
output "frontend_public_ip" {
  value = module.frontend.frontend_public_ip
}

output "frontend_private_ip" {
  value = module.frontend.frontend_private_ip
}

output "backend_public_ip" {
  value = module.backend.backend_public_ip
}

output "backend_private_ip" {
  value = module.backend.backend_private_ip
}