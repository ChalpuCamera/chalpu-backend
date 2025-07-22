

resource "aws_db_subnet_group" "rds_subnet_group" {
  name        = "rds-ec2-db-subnet-group-1"
  description = "Created from the RDS Management Console"
  subnet_ids  = [aws_subnet.rds_pvt_1.id, aws_subnet.rds_pvt_2.id, aws_subnet.rds_pvt_3.id, aws_subnet.rds_pvt_4.id]

  tags = {
    Name = "RDS Subnet Group"
  }
}

resource "aws_db_instance" "chalpudata" {
  identifier             = "chalpudata"
  instance_class         = "db.t4g.micro"
  engine                 = "mysql"
  engine_version         = "8.0.41"
  allocated_storage      = 20
  max_allocated_storage  = 1000
  storage_type           = "gp2"
  username               = "admin"
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids = [aws_security_group.chalpu_db.id]
  parameter_group_name   = "default.mysql8.0"

  multi_az               = false
  publicly_accessible    = true
  storage_encrypted      = true
  kms_key_id             = "arn:aws:kms:ap-northeast-2:279729537494:key/ef85d672-25c4-461f-8724-dab2286208a4"
  backup_retention_period = 1
  deletion_protection    = false
  skip_final_snapshot    = true
  copy_tags_to_snapshot  = true
  auto_minor_version_upgrade = true
} 