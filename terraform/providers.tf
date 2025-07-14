# main.tf

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# AWS Provider 설정
# region을 서울(ap-northeast-2)로 설정합니다.
provider "aws" {
  region = "ap-northeast-2"
} 