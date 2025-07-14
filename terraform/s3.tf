# S3 Bucket for photos
# A basic definition to allow for import. Details will be synced after planning.
resource "aws_s3_bucket" "photo_bucket" {
  bucket = "chalpu-photo-bucket"
}

# S3 Bucket Policy
# This policy allows CloudFront to read objects from the bucket via OAC.
resource "aws_s3_bucket_policy" "photo_bucket_policy" {
  bucket = aws_s3_bucket.photo_bucket.id
  policy = jsonencode({
    Version   = "2008-10-17"
    Id        = "PolicyForCloudFrontPrivateContent"
    Statement = [
      {
        Sid       = "AllowCloudFrontServicePrincipal"
        Effect    = "Allow"
        Principal = {
          Service = "cloudfront.amazonaws.com"
        }
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.photo_bucket.arn}/*"
        Condition = {
          ArnLike = {
            "AWS:SourceArn" = aws_cloudfront_distribution.s3_distribution.arn
          }
        }
      }
    ]
  })
} 