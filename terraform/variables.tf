variable "region" {
  description = "Região da AWS"
  type        = string
  default     = "sa-east-1"
}

variable "dynamo_table_arn" {
  description = "ARN da tabela DynamoDB usada pelas Lambdas"
  type        = string
  default     = "arn:aws:dynamodb:sa-east-1:419939494689:table/ListaMercado"
}
