# /terraform/network.tf

# AWS에 이미 존재하는 VPC 리소스를 정의합니다.
# 이 코드는 리소스를 "생성"하는 것이 아니라,
# Terraform이 관리할 대상(Existing VPC)을 알려주는 역할을 합니다.
resource "aws_vpc" "main" {
  # AWS에서 확인한 실제 VPC의 CIDR 블록을 입력합니다.
  cidr_block = "172.31.0.0/16"

  # 이 외 다른 설정들 (tags 등)도 실제 리소스와 동일하게 맞춰줘야 합니다.
  # terraform plan 결과, 실제 리소스에는 태그가 없으므로 코드에서도 삭제합니다.
}

# 기본 서브넷 4개
resource "aws_subnet" "default_2b" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "172.31.16.0/20"
  availability_zone       = "ap-northeast-2b"
  map_public_ip_on_launch = false
}

resource "aws_subnet" "default_2c" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "172.31.32.0/20"
  availability_zone       = "ap-northeast-2c"
  map_public_ip_on_launch = false
}

resource "aws_subnet" "default_2a" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "172.31.0.0/20"
  availability_zone       = "ap-northeast-2a"
  map_public_ip_on_launch = false
}

resource "aws_subnet" "default_2d" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "172.31.48.0/20"
  availability_zone       = "ap-northeast-2d"
  map_public_ip_on_launch = false
}

# RDS Private 서브넷 4개
resource "aws_subnet" "rds_pvt_1" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "172.31.64.0/25"
  availability_zone = "ap-northeast-2c"
  tags = {
    Name = "RDS-Pvt-subnet-1"
  }
}

resource "aws_subnet" "rds_pvt_2" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "172.31.64.128/25"
  availability_zone = "ap-northeast-2a"
  tags = {
    Name = "RDS-Pvt-subnet-2"
  }
}

resource "aws_subnet" "rds_pvt_3" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "172.31.65.0/25"
  availability_zone = "ap-northeast-2b"
  tags = {
    Name = "RDS-Pvt-subnet-3"
  }
}

resource "aws_subnet" "rds_pvt_4" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "172.31.65.128/25"
  availability_zone       = "ap-northeast-2d"
  tags = {
    Name = "RDS-Pvt-subnet-4"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
  # 태그는 plan을 통해 확인 후 추가/수정합니다.
}


# 1. 'RDS-Pvt-rt' 라우팅 테이블
resource "aws_route_table" "rds_pvt" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "RDS-Pvt-rt"
  }
}

# 2. Main 라우팅 테이블
resource "aws_route_table" "main" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  # 태그는 없으므로 비워둡니다.
}

# 3. 'RDS-Pvt-rt'에 서브넷 연결 (Association)
resource "aws_route_table_association" "rds_pvt_1" {
  subnet_id      = aws_subnet.rds_pvt_1.id
  route_table_id = aws_route_table.rds_pvt.id
}
resource "aws_route_table_association" "rds_pvt_2" {
  subnet_id      = aws_subnet.rds_pvt_2.id
  route_table_id = aws_route_table.rds_pvt.id
}
resource "aws_route_table_association" "rds_pvt_3" {
  subnet_id      = aws_subnet.rds_pvt_3.id
  route_table_id = aws_route_table.rds_pvt.id
}
resource "aws_route_table_association" "rds_pvt_4" {
  subnet_id      = aws_subnet.rds_pvt_4.id
  route_table_id = aws_route_table.rds_pvt.id
} 