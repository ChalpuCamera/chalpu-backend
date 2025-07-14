# /terraform/iam.tf

# EC2 인스턴스에 연결된 IAM Instance Profile을 정의합니다.
resource "aws_iam_instance_profile" "chalpu_ec2_s3_access" {
  name = "chalpu-ec2-s3-access-role"
  # 실제 역할(role) 자체는 여기서 관리하지 않고,
  # 이름만 참조하여 연결합니다.
  # 역할 자체를 마이그레이션하려면 별도의 aws_iam_role 리소스 정의가 필요합니다.
  role = "chalpu-ec2-s3-access-role"
} 