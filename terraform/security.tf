# /terraform/security.tf

# =======================================================
#               Security Group (본체)
# =======================================================

# 1. ec2-rds-1 (sg-03c25e3eb371e6475)
resource "aws_security_group" "ec2_rds_1" {
  name        = "ec2-rds-1"
  description = "Security group attached to instances to securely connect to chalpuDB. Modification could lead to connection loss."
  vpc_id      = aws_vpc.main.id
  tags = { Name = "ec2-rds-1" }
}

# 2. ChalpuDB (sg-01836bd376531a770)
resource "aws_security_group" "chalpu_db" {
  name        = "ChalpuDB"
  description = "rds"
  vpc_id      = aws_vpc.main.id
  tags = { Name = "ChalpuDB" }
}

# 3. default (sg-007b97f85e85a5ae6)
resource "aws_security_group" "default" {
  name        = "default"
  description = "default VPC security group"
  vpc_id      = aws_vpc.main.id
  tags = { Name = "default" }
}

# 4. rds-ec2-1 (sg-0cb9c07645651c978)
resource "aws_security_group" "rds_ec2_1" {
  name        = "rds-ec2-1"
  description = "Security group attached to chalpuDB to allow EC2 instances with specific security groups attached to connect to the database. Modification could lead to connection loss."
  vpc_id      = aws_vpc.main.id
  tags = { Name = "rds-ec2-1" }
}

# 5. launch-wizard-2 (sg-035145628b27f1dd3)
resource "aws_security_group" "launch_wizard_2" {
  name        = "launch-wizard-2"
  description = "launch-wizard-2 created 2025-06-30T02:50:08.084Z"
  vpc_id      = aws_vpc.main.id
  tags = { Name = "launch-wizard-2" }
}

# 6. launch-wizard-1 (sg-033a184296a5703d6)
resource "aws_security_group" "launch_wizard_1" {
  name        = "launch-wizard-1"
  description = "launch-wizard-1 created 2025-06-18T06:51:22.139Z"
  vpc_id      = aws_vpc.main.id
  tags = { Name = "launch-wizard-1" }
}


# =======================================================
#          Security Group Rules (규칙 분리)
# =======================================================

# -- ec2-rds-1 (sg-03c25e3eb371e6475)의 규칙 --
# Egress: rds-ec2-1의 3306 포트로 아웃바운드 허용
resource "aws_security_group_rule" "ec2_to_rds" {
  type                     = "egress"
  from_port                = 3306
  to_port                  = 3306
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.rds_ec2_1.id
  security_group_id        = aws_security_group.ec2_rds_1.id
  description              = "Rule to allow connections to chalpuDB from any instances this security group is attached to"
}


# -- ChalpuDB (sg-01836bd376531a770)의 규칙 --
# Ingress: 모든 IP에서 3306 포트로 인바운드 허용
resource "aws_security_group_rule" "chalpu_db_ingress" {
  type              = "ingress"
  from_port         = 3306
  to_port           = 3306
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.chalpu_db.id
}
# Egress: 모든 IP로 아웃바운드 허용
resource "aws_security_group_rule" "chalpu_db_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.chalpu_db.id
}


# -- default (sg-007b97f85e85a5ae6)의 규칙 --
# Ingress: 자기 자신으로부터의 모든 트래픽 허용
resource "aws_security_group_rule" "default_ingress" {
  type                     = "ingress"
  from_port                = 0
  to_port                  = 0
  protocol                 = "-1"
  self                     = true
  security_group_id        = aws_security_group.default.id
}
# Egress: 모든 IP로 아웃바운드 허용
resource "aws_security_group_rule" "default_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.default.id
}


# -- rds-ec2-1 (sg-0cb9c07645651c978)의 규칙 --
# Ingress: ec2-rds-1 그룹에서 3306 포트로 인바운드 허용
resource "aws_security_group_rule" "rds_from_ec2" {
  type                     = "ingress"
  from_port                = 3306
  to_port                  = 3306
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.ec2_rds_1.id
  security_group_id        = aws_security_group.rds_ec2_1.id
  description              = "Rule to allow connections from EC2 instances with sg-03c25e3eb371e6475 attached"
}
# Ingress: 모든 IP에서 3306 포트로 인바운드 허용
resource "aws_security_group_rule" "rds_from_any" {
  type              = "ingress"
  from_port         = 3306
  to_port           = 3306
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.rds_ec2_1.id
}


# -- launch-wizard-2 (sg-035145628b27f1dd3)의 규칙 --
resource "aws_security_group_rule" "lw2_ingress_ssh" {
  type              = "ingress"
  from_port         = 22
  to_port           = 22
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.launch_wizard_2.id
}
resource "aws_security_group_rule" "lw2_ingress_http" {
  type              = "ingress"
  from_port         = 80
  to_port           = 80
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.launch_wizard_2.id
}
resource "aws_security_group_rule" "lw2_ingress_https" {
  type              = "ingress"
  from_port         = 443
  to_port           = 443
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.launch_wizard_2.id
}
resource "aws_security_group_rule" "lw2_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.launch_wizard_2.id
}

# -- launch-wizard-1 (sg-033a184296a5703d6)의 규칙 --
resource "aws_security_group_rule" "lw1_ingress_ssh" {
  type              = "ingress"
  from_port         = 22
  to_port           = 22
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.launch_wizard_1.id
}
resource "aws_security_group_rule" "lw1_ingress_http" {
  type              = "ingress"
  from_port         = 80
  to_port           = 80
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.launch_wizard_1.id
}
resource "aws_security_group_rule" "lw1_ingress_https" {
  type              = "ingress"
  from_port         = 443
  to_port           = 443
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.launch_wizard_1.id
}
resource "aws_security_group_rule" "lw1_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.launch_wizard_1.id
} 