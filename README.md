# Sentinel

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

Sentinel é uma plataforma backend de gerenciamento de incidentes construída com Spring Boot, Spring Data JPA, Flyway, Spring Security e JWT.

## Principais Recursos

- Gestão de incidentes
- Controle de acesso baseado em roles
- Integrações externas autenticadas por JWT
- Histórico completo de alterações
- Logs de incidentes
- Geração de PDF
- Verificação automática de SLA

## Fluxo de Trabalho

1. Um incidente é criado manualmente ou por integração.
2. O incidente inicia com status OPEN.
3. Técnicos atualizam o status durante a investigação.
4. Todas as alterações são registradas no histórico.
5. Logs podem ser associados ao incidente.
6. O scheduler monitora violações de SLA.
7. O incidente é encerrado após resolução.

## Arquitetura

```text
Client   
  │   
  ▼ 
Spring Security (JWT)   
  │   
  ▼ 
Controller   
  │   
  ▼ 
Service   
  │   
  ▼ 
Repository   
  │   
  ▼ 
PostgreSQL
```

## O que é um incidente?

Um incidente representa uma falha ou indisponibilidade em um serviço monitorado.

Cada incidente possui:

- Título
- Descrição
- Severidade
- Status
- SLA

O objetivo da plataforma é centralizar o gerenciamento desses incidentes e permitir acompanhamento do ciclo de vida até sua resolução.

## Modelo de Dados

```text
Organization
     |
     +--- Users
     |
     +--- Incidents
              |
              +--- IncidentHistory
              |
              +--- IncidentLog
```

## Papéis

### ADMIN
- Gerencia usuários
- Gerencia integrações
- Acessa e atualiza incidentes

### TECH
- Atualiza incidentes
- Atua na resolução de incidentes

### USER
- Consulta incidentes

### SYSTEM
- Utilizado para integrações externas autenticadas via token

## Visão Geral

- Base: Java 25
- Build: Maven com `mvnw` / `mvnw.cmd`
- Banco de dados: PostgreSQL
- Migrações: Flyway
- Autenticação: JWT
- Documentação OpenAPI: Springdoc / Swagger UI
- Scheduler: verificação periódica de SLA em incidentes

## Estrutura do Projeto

Pontos principais do pacote `src/main/java/com/sentinel/sentinel`:

- `SentinelApplication.java` - ponto de entrada Spring Boot e habilita agendamento (`@EnableScheduling`)
- `controllers/v1` - endpoints REST de autenticação, usuários, integrações, incidentes, logs e histórico
- `services` - regras de negócio e geração de tokens
- `models` - entidades JPA e objetos de domínio
- `repositories` - interfaces Spring Data JPA
- `infra/security` - segurança JWT e configuração do Spring Security
- `infra/springdoc` - configuração de documentação OpenAPI
- `schedulers` - tarefas agendadas
- `dto` - objetos de transferência de dados para requisições e respostas

## Funcionalidades

✅ Autenticação JWT

✅ Controle de acesso por roles

✅ Gestão de incidentes

✅ Histórico de alterações

✅ Logs de incidentes

✅ Integração entre sistemas

✅ Recuperação de senha via código de redefinição

✅ Geração de PDF

✅ Verificação automática de SLA

✅ Documentação OpenAPI

## Diferenciais

- Controle de acesso baseado em roles
- Geração de PDF para auditoria
- Histórico completo de alterações de incidentes
- Integração via credenciais de sistema
- Scheduler para monitoramento de SLA
- Filtros avançados e paginação

## Tecnologias

- Java 25
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT
- Maven
- Docker
- OpenAPI / Swagger
- JUnit 5
- Mockito

## Qualidade

- Testes unitários com JUnit 5 e Mockito
- Testes de integração com MockMvc
- Validação de entrada com Bean Validation
- Tratamento global de exceções
- Migrações versionadas com Flyway

## Dependências principais

- `spring-boot-starter-data-jpa`
- `spring-boot-starter-webmvc`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-boot-starter-mail`
- `spring-boot-starter-flyway`
- `postgresql`
- `java-jwt`
- `springdoc-openapi-starter-webmvc-ui`
- `openpdf`

## Configuração

Há um arquivo de exemplo em `src/main/resources/application.properties.example`.
Copie para `src/main/resources/application.properties` e ajuste conforme o ambiente.

Valores padrões esperados para desenvolvimento local:

```properties
spring.application.name=sentinel
api.version=v1
spring.datasource.url=jdbc:postgresql://localhost:5432/sentinel
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.show-sql=true
api.security.token.secret=secret
api.security.token.duration=24
```

## Executando o Projeto

### 1. Com Docker Compose

O projeto inclui `docker-compose.yml` para iniciar um PostgreSQL local:

```bash
docker compose up -d
```

O serviço PostgreSQL expõe a porta `5432` e utiliza:

- usuário: `postgres`
- senha: `password`

### 2. Build e execução

No Windows:

```powershell
./mvnw.cmd clean package
./mvnw.cmd spring-boot:run
```

No Linux/macOS ou bash:

```bash
./mvnw clean package
./mvnw spring-boot:run
```

### 3. Testes

Execute os testes com:

```bash
./mvnw test
```

## Endpoints Principais

### Autenticação

- `POST /v1/auth/register` - criar usuário root inicial
- `POST /v1/auth/login` - autenticar usuário e receber JWT
- `POST /v1/auth/token` - gerar token para integração de sistema
- `POST /v1/auth/forgot_password/reset_code` - solicitar código de redefinição de senha
- `POST /v1/auth/forgot_password/reset_password` - redefinir senha usando o código enviado

### Usuários

- `POST /v1/users` - criar novo usuário (requer `ROLE_ADMIN`)

### Integrações de Sistema

- `POST /v1/systemIntegration` - criar credenciais de integração externa (requer `ROLE_ADMIN`)

### Incidentes

- `POST /v1/incidents` - criar incidente manual
- `POST /v1/incidents/system_integration` - criar incidente via integração de sistema (`ROLE_SYSTEM`)
- `GET /v1/incidents/{id}` - buscar incidente por ID
- `GET /v1/incidents` - listar incidentes com filtros e paginação
- `PUT /v1/incidents` - atualizar incidente (requer `ROLE_TECH`)
- `GET /v1/incidents/pdf` - gerar PDF de incidentes

### Histórico de Incidentes

- `GET /v1/incidentHistory` - listar histórico de incidentes com filtros
- `GET /v1/incidentHistory/pdf` - gerar PDF do histórico

### Logs de Incidentes

- `GET /v1/incidentLog` - listar logs de incidentes com filtros
- `GET /v1/incidentLog/pdf` - gerar PDF de logs

## Exemplos de Payload

### Autenticação

`POST /v1/auth/register`
```json
{
  "name": "Admin Root",
  "email": "admin@example.com",
  "password": "StrongPassword123",
  "organizationName": "Example Corp"
}
```

`POST /v1/auth/login`
```json
{
  "email": "admin@example.com",
  "password": "StrongPassword123"
}
```

`POST /v1/auth/token`
```json
{
  "clientId": "integration-client-id",
  "clientSecret": "integration-client-secret"
}
```

### Usuários

`POST /v1/users`
```json
{
  "name": "Tech User",
  "email": "tech@example.com",
  "password": "AnotherStrongPassword123",
  "organizationId": 1,
  "role": "ADMIN"
}
```

### Integrações de Sistema

`POST /v1/systemIntegration`
```json
{
  "name": "My External System"
}
```

### Incidentes

`POST /v1/incidents`
```json
{
  "title": "Service outage in payment gateway",
  "description": "Customers are unable to complete payments.",
  "severity": "CRITICAL",
  "serviceName": "payment-gateway"
}
```

`POST /v1/incidents/system_integration`
```json
{
  "incident": {
    "title": "Automated alert: disk full",
    "description": "Disk usage exceeded 95% on server.",
    "severity": "HIGH",
    "serviceName": "storage-service"
  },
  "message": "Disk usage alert from monitoring system",
  "incidentLogLevel": "WARN",
  "stacktrace": "Optional stack trace text"
}
```

`PUT /v1/incidents`
```json
{
  "incidentId": 10,
  "title": "Updated incident title",
  "description": "Updated description with more details.",
  "severity": "MEDIUM",
  "serviceName": "payment-gateway",
  "incidentStatus": "UNDER_REVIEW"
}
```

### Cabeçalhos de Autenticação

Para endpoints protegidos, envie o JWT no cabeçalho:

```
Authorization: Bearer <token>
```

### Exemplos de query params

`GET /v1/incidents`
```
/v1/incidents?page=0&size=10&title=payment&severity=HIGH&status=OPEN&serviceName=payment-gateway&slaViolate=false
```

`GET /v1/incidentHistory`
```
/v1/incidentHistory?page=0&size=10&incidentId=1&newStatus=RESOLVED&from=2026-06-01T00:00:00Z&to=2026-06-22T00:00:00Z&userId=2
```

`GET /v1/incidentLog`
```
/v1/incidentLog?page=0&size=20&incidentId=1&incidentLogLevel=ERROR&message=disk&serviceName=storage-service&from=2026-06-22T00:00:00Z&to=2026-06-22T12:00:00Z&userId=3
```

### Valores de Enum suportados

- `severity`: `CRITICAL`, `HIGH`, `MEDIUM`, `LOW`
- `role`: `ADMIN`, `TECH`, `USER`, `SYSTEM`
- `incidentStatus`: `OPEN`, `UNDER_REVIEW`, `IN_CORRECTION`, `RESOLVED`, `CLOSED`
- `incidentLogLevel`: `INFO`, `WARN`, `ERROR`

## Segurança

- Todos os endpoints `/v1/**` exigem autenticação JWT, exceto:
  - `/v1/auth/register`
  - `/v1/auth/login`
  - `/v1/auth/token`
- Swagger UI e OpenAPI também estão liberados publicamente.
- O token é verificado em `SecurityFilter` e rotas são protegidas no `SecurityConfiguration`.
- Há hierarquia de papéis:
  - `ROLE_ADMIN > ROLE_TECH > ROLE_USER`

## Documentação API

Após iniciar a aplicação, a documentação OpenAPI fica disponível em:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Agendador

A aplicação inclui um scheduler em `src/main/java/com/sentinel/sentinel/schedulers/IncidentScheduler.java`.
Ele executa `incidentService.verifyExpiredSla()` a cada minuto (`cron = "0 * * * * *"`).

## Observações

- As migrações de banco de dados estão em `src/main/resources/db/migration`.
- Se quiser rodar em outro banco, ajuste `spring.datasource.url`, usuário e senha.
- O JWT secret deve ser alterado em produção.

## Aprendizados

Durante o desenvolvimento deste projeto foram aplicados conceitos como:

- Spring Security
- JWT Authentication
- Role Based Access Control (RBAC)
- Paginação e filtros dinâmicos
- Scheduler com Spring
- Migrações com Flyway
- Testes unitários e integração
- Geração de documentos PDF

---