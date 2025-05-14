output "lambda_functions" {
  description = "Lista das funções Lambda criadas"
  value = {
    for name, lambda in aws_lambda_function.lambdas :
    name => lambda.arn
  }
}

output "role_name" {
  description = "Nome da role de execução da Lambda"
  value       = aws_iam_role.lambda_exec.name
}
