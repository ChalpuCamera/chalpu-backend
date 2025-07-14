# CloudFront Origin Access Control (OAC)
# This allows CloudFront to securely access the S3 bucket.
resource "aws_cloudfront_origin_access_control" "default" {
  name                              = "oac-chalpu-photo-bucket.s3.ap-northeast-2.amazonaws.-mcofb0j2340"
  description                       = "Created by CloudFront"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

# CloudFront Distribution
resource "aws_cloudfront_distribution" "s3_distribution" {
  enabled      = true
  is_ipv6_enabled = true
  price_class  = "PriceClass_200"
  http_version = "http2"

  aliases = ["cdn.chalpu.com"]

  tags = {
    Name = "chalpu-cloudfront"
  }

  origin {
    domain_name              = aws_s3_bucket.photo_bucket.bucket_regional_domain_name
    origin_access_control_id = aws_cloudfront_origin_access_control.default.id
    origin_id                = "chalpu-photo-bucket.s3.ap-northeast-2.amazonaws.com-mcofarg7td3"
  }

  default_cache_behavior {
    allowed_methods        = ["GET", "HEAD"]
    cached_methods         = ["GET", "HEAD"]
    target_origin_id       = "chalpu-photo-bucket.s3.ap-northeast-2.amazonaws.com-mcofarg7td3"
    viewer_protocol_policy = "redirect-to-https"
    compress               = true
    cache_policy_id        = "658327ea-f89d-4fab-a63d-7e88639e58f6" # Managed policy: CachingOptimized
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    acm_certificate_arn      = "arn:aws:acm:us-east-1:279729537494:certificate/419d1bdf-54e5-43a3-937e-28a0f11d5830"
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1.2_2021"
  }
} 