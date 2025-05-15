# global_main.tf
module "backend" {
  source        = "./modules/backend"
  instance_type = var.instance_type
  image_id      = var.image_id
  key_name      = var.key_name
  iam_role_name = aws_iam_role.ec2_role.name
  profile_name  = aws_iam_instance_profile.ec2_role.name
  sg_name       = aws_security_group.sg.name
  depends_on    = [aws_iam_role.ec2_role]
}

module "frontend" {
  source        = "./modules/frontend"
  instance_type = var.instance_type
  image_id      = var.image_id
  key_name      = var.key_name
  iam_role_name = aws_iam_role.ec2_role.name
  profile_name  = aws_iam_instance_profile.ec2_role.name
  sg_name       = aws_security_group.sg.name
  backend_private_ip  = module.backend.backend_public_ip
  depends_on    = [module.backend]
}













resource "aws_security_group" "sg" {
  name = "universal_sg"

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "sg"
  }
}

resource "aws_iam_role" "ec2_role" {
  name = "EC2InstanceRole"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Principal = {
          Service = "ec2.amazonaws.com"
        },
        Effect = "Allow",
        Sid    = ""
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ec2_role_policy" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ReadOnlyAccess"
}

resource "aws_iam_instance_profile" "ec2_role" {
  name = "EC2InstanceProfile"
  role = aws_iam_role.ec2_role.name
}