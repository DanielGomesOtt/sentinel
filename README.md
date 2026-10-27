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

O repositório está organizado em formato monorepo, separando a API backend da interface web:

```text
sentinel/
├── backend/            # Aplicação Backend (Spring Boot 3 / Java 25)
│   ├── src/main/java/com/sentinel/sentinel/
│   │   ├── controllers/v1/   # Endpoints REST (Auth, Incidents, Users, Logs, etc.)
│   │   ├── dto/              # Records e objetos de transferência de dados (DTOs)
│   │   ├── enums/            # Enumerações de domínio (Status, Severity, Roles, LogLevel)
│   │   ├── exceptions/       # Exceções customizadas e mapeamento de erros
│   │   ├── infra/            # Segurança JWT, handlers globais e OpenAPI
│   │   ├── models/           # Entidades JPA mapeadas para o banco PostgreSQL
│   │   ├── repositories/     # Interfaces de acesso aos dados (Spring Data JPA)
│   │   ├── schedulers/       # Tarefas agendadas (verificação automática de SLA)
│   │   ├── services/         # Regras de negócio, geração de relatórios e tokens
│   │   ├── specifications/   # Filtros dinâmicos flexíveis com JPA Specifications
│   │   └── utils/            # Utilitários (PDF, formatações e versionamento)
│   ├── src/main/resources/
│   │   ├── db/migration/     # Scripts de migração de esquema e dados (Flyway)
│   │   └── application.properties.example
│   ├── Dockerfile            # Construção da imagem Docker do backend
│   └── pom.xml               # Gerenciador de dependências e build Maven
├── frontend/           # Interface Web (React 19 / Vite / Tailwind CSS - gerada por IA)
│   ├── src/
│   │   ├── components/       # Componentes de interface (modais, badges, paginação)
│   │   ├── context/          # Contextos globais (AuthContext, ThemeContext)
│   │   ├── pages/            # Telas da aplicação (Login, Incidentes, Histórico, Logs, Admin)
│   │   └── services/         # Cliente HTTP nativo (apiClient.js) com suporte a JWT e PDF
│   ├── package.json          # Dependências e scripts do frontend
│   └── vite.config.js        # Configuração do Vite e proxy reverso para a API
├── docker-compose.yml  # Orquestração do banco de dados PostgreSQL
├── .env.example        # Modelo de variáveis de ambiente do Docker
└── README.md           # Documentação completa do projeto
```

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
- React
- Vite
- Tailwind CSS
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

## Frontend (Interface Web)

> [!NOTE]
> **Aviso sobre o Frontend**: A interface web presente no diretório `frontend/` foi **100% gerada com auxílio de Inteligência Artificial**. Como o objetivo principal do Sentinel é demonstrar arquitetura, segurança, regras de negócio e padrões no **backend (Java / Spring Boot)**, o frontend foi concebido exclusivamente como uma ferramenta complementar de apoio visual para facilitar a demonstração, navegação e testes práticos de todos os recursos, fluxos de autenticação e relatórios da API, não se tratando do foco principal do projeto.

### Características do Frontend

- **Tecnologias**: React 19, Vite, Tailwind CSS e Lucide React.
- **Integração com a API**: Consome todos os endpoints através de um cliente HTTP nativo em `frontend/src/services/apiClient.js`, gerenciando o envio do token JWT (`Authorization: Bearer <token>`) e download direto dos relatórios em PDF.
- **Proxy Reverso Integrado**: Configurado em `frontend/vite.config.js` para redirecionar chamadas iniciadas em `/v1` diretamente para o backend em `http://localhost:8080`, dispensando configuração manual de CORS no desenvolvimento.
- **Funcionalidades da Interface**:
  - Tela de Autenticação (Login, Registro do Admin inicial e Recuperação de Senha via código).
  - Gestão de Incidentes (Criação com cálculo automático de SLA, Edição com controle de status, Filtros avançados e Geração de PDF).
  - Linha do Tempo de Histórico (Auditoria completa das ações e transições de status com exportação em PDF).
  - Consulta de Logs de Incidentes (Filtros operacionais por nível de severidade e exportação em PDF).
  - Painel Administrativo (Cadastro de novos usuários com atribuição de papéis e geração de credenciais para integrações externas).

## Configuração

Há um arquivo de exemplo em `backend/src/main/resources/application.properties.example`.
Copie para `backend/src/main/resources/application.properties` e ajuste conforme o ambiente.

Valores padrões esperados para desenvolvimento local:

```properties
spring.application.name=sentinel
api.version=v1
spring.datasource.url=jdbc:postgresql://localhost:5432/sentinel
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.show-sql=true

# Envio de e-mail (Mailtrap / SMTP via .env)
spring.mail.host=${SPRING_MAIL_SENDER_HOST}
spring.mail.port=${SPRING_MAIL_SENDER_PORT}
spring.mail.username=${SPRING_MAIL_SENDER_USERNAME}
spring.mail.password=${SPRING_MAIL_SENDER_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
smtp.mail=${SMTP_MAIL}

api.security.token.secret=secret
api.security.token.duration=24
```

Além disso, configure as variáveis de ambiente criando o arquivo `.env` a partir do `.env.example`:

```bash
cp .env.example .env
```

Preencha no `.env` tanto as credenciais do banco de dados (PostgreSQL) quanto as do serviço de e-mail (Mailtrap / SMTP).

## Executando o Projeto

Execute os componentes na ordem abaixo, pois o **frontend** depende do **backend**, que por sua vez depende do **banco de dados** estar em execução.

### 1. Banco de Dados (PostgreSQL via Docker)

Inicie o container do banco de dados utilizando o Docker Compose:

```bash
docker compose up -d
```

Comandos úteis para gerenciar o banco:
- Acompanhar logs do container: `docker compose logs -f db`
- Verificar status do container: `docker compose ps`
- Parar o container: `docker compose down`

### 2. Backend (Spring Boot)

Com o banco de dados rodando, inicie a aplicação Spring Boot:

No Windows:

```powershell
cd backend
./mvnw.cmd spring-boot:run
```

No Linux/macOS ou bash:

```bash
cd backend
./mvnw spring-boot:run
```

> A API ficará disponível em `http://localhost:8080`.  
> A documentação Swagger UI estará acessível em `http://localhost:8080/swagger-ui/index.html`.

### 3. Frontend (React + Vite)

Com o backend ativo, abra um novo terminal, instale as dependências e inicie o servidor de desenvolvimento:

```bash
cd frontend
npm install
npm run dev
```

> A aplicação frontend ficará acessível em `http://localhost:3000` (com proxy já configurado para repassar requisições `/v1` ao backend na porta `8080`).

## Testes

Execute os testes no backend com:

```bash
cd backend
./mvnw clean test
```

## Endpoints Principais

A API é versionada sob o prefixo `/v1`. Todos os endpoints protegidos exigem o envio do token JWT no cabeçalho `Authorization: Bearer <token>`.

### Autenticação (`/v1/auth`)

| Método | Endpoint | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/v1/auth/register` | Público | Registra o primeiro usuário administrador root da organização |
| `POST` | `/v1/auth/login` | Público | Autentica o usuário (e-mail/senha) e retorna perfil com JWT |
| `POST` | `/v1/auth/token` | Público | Gera token JWT de acesso para integrações de sistema (Client Credentials) |
| `POST` | `/v1/auth/forgot_password/reset_code` | Público | Solicita envio de código de 6 dígitos para redefinição de senha |
| `POST` | `/v1/auth/forgot_password/reset_password` | Público | Redefine a senha do usuário validando o código de verificação recebido |

### Usuários (`/v1/users`)

| Método | Endpoint | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/v1/users` | `ROLE_ADMIN` | Cria um novo usuário (`ADMIN`, `TECH` ou `USER`) vinculado à organização |

### Integrações de Sistema (`/v1/systemIntegration`)

| Método | Endpoint | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/v1/systemIntegration` | `ROLE_ADMIN` | Cadastra uma nova integração externa e gera credenciais (`clientId` e `clientSecret`) |

### Incidentes (`/v1/incidents`)

| Método | Endpoint | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/v1/incidents` | Autenticado | Cria um novo incidente manual (calcula SLA automaticamente) |
| `POST` | `/v1/incidents/system_integration` | `ROLE_SYSTEM` | Cria um incidente automatizado via integração de sistema com log opcional |
| `GET` | `/v1/incidents/{id}` | Autenticado | Busca incidente por ID (`USER` acessa apenas os seus; `TECH`/`ADMIN` acessam todos) |
| `GET` | `/v1/incidents` | Autenticado | Lista incidentes da organização com filtros dinâmicos e paginação |
| `PUT` | `/v1/incidents` | `ROLE_TECH` | Atualiza dados do incidente e transita status (valida sequência de etapas) |
| `GET` | `/v1/incidents/pdf` | Autenticado | Gera e baixa relatório em PDF dos incidentes filtrados |

### Histórico de Incidentes (`/v1/incidentHistory`)

| Método | Endpoint | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/v1/incidentHistory` | Autenticado | Lista histórico de alterações de um incidente com filtros e paginação |
| `GET` | `/v1/incidentHistory/pdf` | Autenticado | Gera e baixa relatório em PDF do histórico do incidente |

### Logs de Incidentes (`/v1/incidentLog`)

| Método | Endpoint | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/v1/incidentLog` | Autenticado | Consulta logs de incidentes com filtros (severidade, serviço, data, mensagem) |
| `GET` | `/v1/incidentLog/pdf` | Autenticado | Gera e baixa relatório em PDF dos logs filtrados |

## Regras de Negócio e Ciclo de Vida

### 1. Ciclo de Vida e Transições de Status

Os incidentes seguem um fluxo estruturado de estados:

```text
OPEN ──► UNDER_REVIEW ──► IN_CORRECTION ──► RESOLVED ──► CLOSED
```

- **Transições sequenciais para `TECH`**: Usuários com perfil técnico só podem transitar entre status adjacentes (avançar ou retroceder exatamente um passo por vez: ex. `OPEN` <-> `UNDER_REVIEW` <-> `IN_CORRECTION` <-> `RESOLVED`). Não podem pular etapas nem encerrar (`CLOSED`) diretamente.
- **Privilégios de `ADMIN`**: Administradores podem realizar transições livres entre qualquer status e são os únicos que podem finalizar o incidente com status `CLOSED`.
- **Imutabilidade**: Incidentes com status `CLOSED` tornam-se finais e não podem ser atualizados.
- **Rastreabilidade**: Toda alteração de dados ou avanço de status registra automaticamente uma entrada no `IncidentHistory`.

### 2. Prazos de SLA (Service Level Agreement)

Ao criar um incidente, o prazo de SLA (`slaDeadline`) é calculado automaticamente somando a duração da severidade ao timestamp atual:

| Severidade | Prazo de SLA |
| :--- | :--- |
| `CRITICAL` | 4 horas |
| `HIGH` | 24 horas |
| `MEDIUM` | 48 horas |
| `LOW` | 72 horas |

- **Verificação Automática (Scheduler)**: A cada minuto (`cron = "0 * * * * *"`), o agendador verifica incidentes não encerrados que ultrapassaram a data limite (`slaDeadline < agora`). Ao identificar violação, marca automaticamente `slaViolate = true` e registra o evento `"sla violated"` no histórico.

### 3. Regras de Visibilidade

- **`ROLE_USER`**: Usuários comuns visualizam e consultam exclusivamente os incidentes criados por eles mesmos.
- **`ROLE_TECH` e `ROLE_ADMIN`**: Possuem visibilidade sobre todos os incidentes pertencentes à organização.

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
  "clientId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "clientSecret": "sec_7a8f9b0c1d2e3f4a5b6c"
}
```

`POST /v1/auth/forgot_password/reset_code`
```json
{
  "email": "admin@example.com"
}
```

`POST /v1/auth/forgot_password/reset_password`
```json
{
  "code": "123456",
  "email": "admin@example.com",
  "newPassword": "NewStrongPassword123"
}
```

### Usuários

`POST /v1/users` *(Requer `ROLE_ADMIN`)*
```json
{
  "name": "Tech User",
  "email": "tech@example.com",
  "password": "AnotherStrongPassword123",
  "organizationId": 1,
  "role": "TECH"
}
```

### Integrações de Sistema

`POST /v1/systemIntegration` *(Requer `ROLE_ADMIN`)*
```json
{
  "name": "Payment Monitoring Service"
}
```

### Incidentes

`POST /v1/incidents`
```json
{
  "title": "Service outage in payment gateway",
  "description": "Customers are unable to complete payments via credit card.",
  "severity": "CRITICAL",
  "serviceName": "payment-gateway"
}
```

`POST /v1/incidents/system_integration` *(Requer `ROLE_SYSTEM`)*
```json
{
  "incident": {
    "title": "Automated alert: high disk usage",
    "description": "Disk usage exceeded 95% on host db-node-01.",
    "severity": "HIGH",
    "serviceName": "storage-service"
  },
  "message": "Disk usage critical threshold alert",
  "incidentLogLevel": "WARN",
  "stacktrace": "Optional stack trace details"
}
```

`PUT /v1/incidents` *(Requer `ROLE_TECH` ou `ROLE_ADMIN`)*
```json
{
  "incidentId": 1,
  "title": "Service outage in payment gateway - Investigation",
  "description": "Identified timeout connecting to acquirer upstream.",
  "severity": "CRITICAL",
  "serviceName": "payment-gateway",
  "incidentStatus": "UNDER_REVIEW"
}
```

### Cabeçalhos de Autenticação

Para endpoints protegidos, envie o JWT no cabeçalho:

```http
Authorization: Bearer <token>
```

### Parâmetros de Consulta (Query Params)

#### `GET /v1/incidents` e `GET /v1/incidents/pdf`
- **Obrigatórios**:
  - `page`: Número da página (inicia em `0`)
  - `size`: Quantidade de registros por página
- **Opcionais**:
  - `title`: Busca parcial por título
  - `description`: Busca parcial por descrição
  - `severity`: `CRITICAL`, `HIGH`, `MEDIUM`, `LOW`
  - `status`: `OPEN`, `UNDER_REVIEW`, `IN_CORRECTION`, `RESOLVED`, `CLOSED`
  - `serviceName`: Nome do serviço afetado
  - `slaDeadline`: Data limite no formato `yyyy-MM-dd HH:mm:ss`
  - `slaViolate`: `true` ou `false`

Exemplo:
```
/v1/incidents?page=0&size=10&severity=HIGH&status=OPEN&serviceName=payment-gateway&slaViolate=false
```

#### `GET /v1/incidentHistory` e `GET /v1/incidentHistory/pdf`
- **Obrigatórios**:
  - `page`: Número da página
  - `size`: Quantidade por página
  - `incidentId`: ID do incidente
- **Opcionais**:
  - `newStatus`: Novo status
  - `previousStatus`: Status anterior
  - `action`: Descrição da ação (ex.: `create incident`, `update incident`, `sla violated`)
  - `from` e `to`: Intervalo de datas no formato ISO-8601 (ex.: `2026-06-01T00:00:00Z`)
  - `userId`: ID do usuário que executou a ação

Exemplo:
```
/v1/incidentHistory?page=0&size=10&incidentId=1&newStatus=RESOLVED&from=2026-06-01T00:00:00Z&to=2026-06-22T00:00:00Z
```

#### `GET /v1/incidentLog` e `GET /v1/incidentLog/pdf`
- **Obrigatórios**:
  - `page`: Número da página
  - `size`: Quantidade por página
- **Opcionais**:
  - `incidentId`: ID do incidente associado
  - `incidentLogLevel`: `INFO`, `WARN`, `ERROR`
  - `message`: Texto parcial da mensagem
  - `serviceName`: Nome do serviço
  - `from` e `to`: Intervalo de datas no formato ISO-8601
  - `userId`: ID do usuário associado

Exemplo:
```
/v1/incidentLog?page=0&size=20&incidentId=1&incidentLogLevel=ERROR&serviceName=storage-service
```

### Valores de Enum suportados

- **`severity`**: `CRITICAL`, `HIGH`, `MEDIUM`, `LOW`
- **`incidentStatus`**: `OPEN`, `UNDER_REVIEW`, `IN_CORRECTION`, `RESOLVED`, `CLOSED`
- **`incidentLogLevel`**: `INFO`, `WARN`, `ERROR`
- **`role`**: `ADMIN`, `TECH`, `USER`, `SYSTEM`

## Segurança

- **Endpoints Públicos**:
  - `POST /v1/auth/register` (criação do usuário root inicial)
  - `POST /v1/auth/login` (autenticação de usuários)
  - `POST /v1/auth/token` (geração de token para integrações de sistema)
  - `POST /v1/auth/forgot_password/reset_code` (solicitação de código de recuperação de senha)
  - `POST /v1/auth/forgot_password/reset_password` (redefinição de senha com código)
  - `/swagger-ui/**` e `/v3/api-docs/**` (documentação OpenAPI/Swagger)
- **Endpoints Protegidos**:
  - Todas as demais rotas `/v1/**` exigem token JWT válido via `SecurityFilter`.
- **Hierarquia de Papéis (Role Hierarchy)**:
  - `ROLE_ADMIN > ROLE_TECH > ROLE_USER`
  - Usuários `ADMIN` herdam automaticamente as permissões de `TECH` e `USER`.
  - Usuários `TECH` herdam as permissões de `USER`.
- **Integrações de Sistema**:
  - Tokens emitidos para integrações de sistema recebem a autoridade `ROLE_SYSTEM`, utilizada para criação automatizada de incidentes via `POST /v1/incidents/system_integration`.

## Documentação API

Após iniciar a aplicação, a documentação interativa Swagger UI e a especificação OpenAPI ficam disponíveis em:

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Agendador

A aplicação inclui um scheduler em `backend/src/main/java/com/sentinel/sentinel/schedulers/IncidentScheduler.java`.
Ele executa `incidentService.verifyExpiredSla()` a cada minuto (`cron = "0 * * * * *"`), verificando violações de SLA de incidentes em aberto.

## Observações

- As migrações de banco de dados estão em `backend/src/main/resources/db/migration`.
- Caso utilize outro banco de dados, ajuste `spring.datasource.url`, usuário e senha no `backend/src/main/resources/application.properties` ou via variáveis de ambiente.
- O segredo JWT (`api.security.token.secret`) deve ser modificado em ambiente de produção.

## Aprendizados

Durante o desenvolvimento deste projeto foram aplicados conceitos como:

- Spring Security e RBAC com hierarquia de permissões
- JWT Authentication para usuários e integrações M2M
- Lifecycle e máquina de estados de incidentes
- Paginação dinâmica e Specification com Spring Data JPA
- Scheduler com Spring para monitoramento automatizado de SLA
- Migrações versionadas de esquema e dados com Flyway
- Testes unitários e de integração com MockMvc e Mockito
- Geração de relatórios em documentos PDF com OpenPDF

---