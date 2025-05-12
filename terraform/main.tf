provider "aws" {
  region = "sa-east-1"
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
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:PutItem",
          "dynamodb:GetItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:Query"
        ]
        Effect   = "Allow"
        Resource = "arn:aws:dynamodb:sa-east-1:419939494689:table/ListaMercado"
      }
    ]
  })
}

resource "aws_lambda_function" "funcao_um" {
  function_name = "funcao-um-java"
  role          = aws_iam_role.lambda_exec.arn
  handler       = "org.example.FuncaoUmHandler::handleRequest"
  runtime       = "java17"
  filename      = "${path.module}/../lambda/funcao-um/target/funcao-um-1.0-SNAPSHOT.jar"
  source_code_hash = filebase64sha256("${path.module}/../lambda/funcao-um/target/funcao-um-1.0-SNAPSHOT.jar")
  timeout       = 10

  depends_on = [
    aws_iam_role_policy_attachment.lambda_basic_execution,
    aws_iam_role_policy.lambda_dynamo_policy
  ]
}

resource "aws_lambda_function" "funcao_dois" {
  function_name = "funcao-dois-java"
  role          = aws_iam_role.lambda_exec.arn
  handler       = "org.example.FuncaoDoisHandler::handleRequest"
  runtime       = "java17"
  filename      = "${path.module}/../lambda/funcao-dois/target/funcao-dois-1.0-SNAPSHOT.jar"
  source_code_hash = filebase64sha256("${path.module}/../lambda/funcao-dois/target/funcao-dois-1.0-SNAPSHOT.jar")
  timeout       = 30

  depends_on = [
    aws_iam_role_policy_attachment.lambda_basic_execution,
    aws_iam_role_policy.lambda_dynamo_policy
  ]
}

resource "aws_lambda_function" "funcao-tres" {
  function_name = "funcao-tres-java"
  role          = aws_iam_role.lambda_exec.arn
  handler       = "org.example.FuncaoTresHandler::handleRequest"
  runtime       = "java17"
  filename      = "${path.module}/../lambda/funcao-tres/target/funcao-tres-1.0-SNAPSHOT.jar"
  source_code_hash = filebase64sha256("${path.module}/../lambda/funcao-tres/target/funcao-tres-1.0-SNAPSHOT.jar")
  timeout       = 30

  depends_on = [
    aws_iam_role_policy_attachment.lambda_basic_execution,
    aws_iam_role_policy.lambda_dynamo_policy
  ]
}

resource "aws_lambda_function" "funcao-quatro" {
  function_name = "funcao-quatro-java"
  role          = aws_iam_role.lambda_exec.arn
  handler       = "org.example.FuncaoQuatroHandler::handleRequest"
  runtime       = "java17"
  filename      = "${path.module}/../lambda/funcao-quatro/target/funcao-quatro-1.0-SNAPSHOT.jar"
  source_code_hash = filebase64sha256("${path.module}/../lambda/funcao-quatro/target/funcao-quatro-1.0-SNAPSHOT.jar")
  timeout       = 30

  depends_on = [
    aws_iam_role_policy_attachment.lambda_basic_execution,
    aws_iam_role_policy.lambda_dynamo_policy
  ]
}

