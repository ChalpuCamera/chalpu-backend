# /terraform/ec2.tf

resource "aws_instance" "chalpu" {
  # 필수 정보
  ami           = "ami-0662f4965dfc70aca"
  instance_type = "t2.micro"

  # 네트워크 설정
  subnet_id              = aws_subnet.default_2c.id
  vpc_security_group_ids = [
    aws_security_group.ec2_rds_1.id,
    aws_security_group.launch_wizard_2.id
  ]
  
  # 연결 설정
  key_name = "soma"

  # IAM 역할 연결
  iam_instance_profile = aws_iam_instance_profile.chalpu_ec2_s3_access.name

  # 태그
  tags = {
    Name = "chalpu"
  }

} 