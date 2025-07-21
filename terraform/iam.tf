# /terraform/iam.tf

# EC2 인스턴스에 연결된 IAM Instance Profile을 정의합니다.
resource "aws_iam_instance_profile" "chalpu_ec2_s3_access" {
  name = "chalpu-ec2-s3-access-role"
  # 실제 역할(role) 자체는 여기서 관리하지 않고,
  # 이름만 참조하여 연결합니다.
  # 역할 자체를 마이그레이션하려면 별도의 aws_iam_role 리소스 정의가 필요합니다.
  role = "chalpu-ec2-s3-access-role"
}

# GitHub Actions OIDC Identity Provider
resource "aws_iam_openid_connect_provider" "github_oidc" {
  url = "https://token.actions.githubusercontent.com"
  
  client_id_list = [
    "sts.amazonaws.com"
  ]
  
  thumbprint_list = [
    "6938fd4d98bab03faadb97b34396831e3780aea1"
  ]
  
  tags = {
    Name = "GitHub-OIDC"
    Environment = "shared"
  }
}

# GitHub Actions IAM Role for Development Environment
resource "aws_iam_role" "github_actions_dev" {
  name = "github-actions-dev-role"
  
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Federated = aws_iam_openid_connect_provider.github_oidc.arn
        }
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringEquals = {
            "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
          }
          StringLike = {
            "token.actions.githubusercontent.com:sub" = "repo:ChalpuCamera/*:ref:refs/heads/dev"
          }
        }
      }
    ]
  })
  
  tags = {
    Name = "GitHub-Actions-Dev"
    Environment = "development"
  }
}

# GitHub Actions IAM Role for Production Environment
resource "aws_iam_role" "github_actions_prod" {
  name = "github-actions-prod-role"
  
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Federated = aws_iam_openid_connect_provider.github_oidc.arn
        }
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringEquals = {
            "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
          }
          StringLike = {
            "token.actions.githubusercontent.com:sub" = "repo:ChalpuCamera/*:ref:refs/heads/main"
          }
        }
      }
    ]
  })
  
  tags = {
    Name = "GitHub-Actions-Prod"
    Environment = "production"
  }
}

# Development Environment Policy
resource "aws_iam_policy" "github_actions_dev_policy" {
  name        = "github-actions-dev-policy"
  description = "Policy for GitHub Actions in development environment"
  
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecr:GetAuthorizationToken",
          "ecr:BatchCheckLayerAvailability",
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "ecr:PutImage"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "ecs:UpdateService",
          "ecs:DescribeServices",
          "ecs:DescribeClusters",
          "ecs:DescribeTaskDefinition",
          "ecs:RegisterTaskDefinition"
        ]
        Resource = "*"
        Condition = {
          StringLike = {
            "ecs:service" = "*dev*"
          }
        }
      },
      {
        Effect = "Allow"
        Action = [
          "iam:PassRole"
        ]
        Resource = "arn:aws:iam::*:role/*ecs*"
      }
    ]
  })
}

# Production Environment Policy
resource "aws_iam_policy" "github_actions_prod_policy" {
  name        = "github-actions-prod-policy"
  description = "Policy for GitHub Actions in production environment"
  
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecr:GetAuthorizationToken",
          "ecr:BatchCheckLayerAvailability",
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "ecr:PutImage"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "ecs:UpdateService",
          "ecs:DescribeServices",
          "ecs:DescribeClusters",
          "ecs:DescribeTaskDefinition",
          "ecs:RegisterTaskDefinition"
        ]
        Resource = "*"
        Condition = {
          StringLike = {
            "ecs:service" = "*prod*"
          }
        }
      },
      {
        Effect = "Allow"
        Action = [
          "iam:PassRole"
        ]
        Resource = "arn:aws:iam::*:role/*ecs*"
      }
    ]
  })
}

# Attach policies to roles
resource "aws_iam_role_policy_attachment" "github_actions_dev_policy_attachment" {
  role       = aws_iam_role.github_actions_dev.name
  policy_arn = aws_iam_policy.github_actions_dev_policy.arn
}

resource "aws_iam_role_policy_attachment" "github_actions_prod_policy_attachment" {
  role       = aws_iam_role.github_actions_prod.name
  policy_arn = aws_iam_policy.github_actions_prod_policy.arn
}