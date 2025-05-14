provider "aws" {
  region = var.region
}

resource "aws_iam_role" "lambda_exec" {
  name = "lambda-exec-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect = "Allow",
      Principal = {
        Service = "lambda.amazonaws.com"
      },
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy" "lambda_dynamo_policy" {
  name = "lambda-dynamo-policy"
  role = aws_iam_role.lambda_exec.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = [
          "dynamodb:PutItem",
          "dynamodb:GetItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:Query"
        ],
        Effect   = "Allow",
        Resource = var.dynamo_table_arn
      }
    ]
  })
}

locals {
  lambdas = [
    {
      name    = "funcao-um-java"
      handler = "org.example.FuncaoUmHandler::handleRequest"
      jar     = "${path.module}/../lambda/funcao-um/target/funcao-um-1.0-SNAPSHOT.jar"
      timeout = 10
    },
    {
      name    = "funcao-dois-java"
      handler = "org.example.FuncaoDoisHandler::handleRequest"
      jar     = "${path.module}/../lambda/funcao-dois/target/funcao-dois-1.0-SNAPSHOT.jar"
      timeout = 30
    },
    {
      name    = "funcao-tres-java"
      handler = "org.example.FuncaoTresHandler::handleRequest"
      jar     = "${path.module}/../lambda/funcao-tres/target/funcao-tres-1.0-SNAPSHOT.jar"
      timeout = 30
    },
    {
      name    = "funcao-quatro-java"
      handler = "org.example.FuncaoQuatroHandler::handleRequest"
      jar     = "${path.module}/../lambda/funcao-quatro/target/funcao-quatro-1.0-SNAPSHOT.jar"
      timeout = 30
    }
  ]
}

resource "aws_lambda_function" "lambdas" {
  for_each      = { for lambda in local.lambdas : lambda.name => lambda }
  function_name = each.value.name
  role          = aws_iam_role.lambda_exec.arn
  handler       = each.value.handler
  runtime       = "java17"
  filename      = each.value.jar
  source_code_hash = filebase64sha256(each.value.jar)
  timeout       = each.value.timeout

  depends_on = [
    aws_iam_role_policy_attachment.lambda_basic_execution,
    aws_iam_role_policy.lambda_dynamo_policy
  ]
}
