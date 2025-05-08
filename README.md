# Projeto AWS Lambda com Terraform

Este projeto tem como objetivo criar uma função AWS Lambda com Terraform, como estudo de infraestrutura como código (IaC).

## Tecnologias utilizadas

- AWS Lambda
- Terraform
- Java

## Como iniciar o projeto

### 1. Pré-requisitos:

- AWS CLI instalado e configurado
- Terraform instalado
- Java 17
- Git

### 2. Clone o repositorio:

```
git clone
cd hello-world-terraform
```

### 3. Realizar o build com Maven

```
mvn clean package
```

O .jar será gerado na pasta target/

### 4. Inicialize o Terrafom

``` 
cd terraform
terraform init
terraform plan
terraform apply
```

---

## Como colaborar
---
Para colaborar com o projeto e propor mudanças é necessario a abertura de Pull Requests (PRs)

### Convenção de nomes de branches

Use o seguinte padrão para nomear suas branches:

- `feature/nome-do-recurso`: novas funcionalidades
- `bugfix/descricao-do-bug`: correção de problemas
- `hotfix/ajuste-crítico`: correções urgentes em produção
- `release/x.y.z`: novo release

### Conventional Commits

Siga o padrão para realizar os commits:

```
<tipo>(escopo opcional): descrição
```

Tipos mais comuns:

- `feat`: nova funcionalidade
- `fix`: correção de bug
- `chore`: tarefas de manutenção (builds, configs, etc.)
- `docs`: apenas documentação
- `refactor`: refatoração de código (sem nova feature ou bug fix)
- `test`: testes
- `style`: formatação, identação, etc. (sem alteração de código funcional)

Exemplo:

```
feat(lambda): adiciona tratamento de erro
```






