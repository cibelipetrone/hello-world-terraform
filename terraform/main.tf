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

resource "aws_lambda_function" "funcao_um" {
  function_name = "funcao-um-java"
  role          = aws_iam_role.lambda_exec.arn
  handler       = "org.example.FuncaoUmHandler::handleRequest"
  runtime       = "java17"
  filename      = "${path.module}/../lambda/funcao-um/target/funcao-um-1.0-SNAPSHOT.jar"
  source_code_hash = filebase64sha256("${path.module}/../lambda/funcao-um/target/funcao-um-1.0-SNAPSHOT.jar")
  timeout       = 10

  depends_on = [aws_iam_role_policy_attachment.lambda_basic_execution]
}

resource "aws_lambda_function" "funcao_dois" {
  function_name = "funcao-dois-java"
  role          = aws_iam_role.lambda_exec.arn
  handler       = "org.example.FuncaoDoisHandler::handleRequest"
  runtime       = "java17"
  filename      = "${path.module}/../lambda/funcao-dois/target/funcao-dois-1.0-SNAPSHOT.jar"
  source_code_hash = filebase64sha256("${path.module}/../lambda/funcao-dois/target/funcao-dois-1.0-SNAPSHOT.jar")
  timeout       = 10

  depends_on = [aws_iam_role_policy_attachment.lambda_basic_execution]
}
