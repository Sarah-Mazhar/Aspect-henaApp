# modules/frontend/main.tf
resource "aws_instance" "frontend" {
  ami                  = var.image_id
  instance_type        = var.instance_type
  security_groups      = [var.sg_name]
  key_name             = var.key_name
  iam_instance_profile = var.profile_name
  associate_public_ip_address = true

user_data = templatefile("${path.module}/frontend-script.sh", {
  backend_private_ip  = var.backend_private_ip
})


  tags = {
    Name = "frontend"
  }
}