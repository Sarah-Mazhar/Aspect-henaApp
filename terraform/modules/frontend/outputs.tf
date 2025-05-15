# modules/frontend/outputs.tf
output "frontend_public_ip" {
  value = aws_instance.frontend.public_ip
}

output "frontend_private_ip" {
  value = aws_instance.frontend.private_ip
}