# modules/backend/main.tf
resource "aws_instance" "backend" {
  ami                  = var.image_id
  instance_type        = var.instance_type
  security_groups      = [var.sg_name]
  key_name             = var.key_name
  iam_instance_profile = var.profile_name
  user_data            = filebase64(("${path.module}/backend-script.sh"))

  tags = {
    Name = "backend"
  }
}