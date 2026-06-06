# Project Overview

## Summary

A API de Gerenciamento de Pessoas é um serviço REST que permite cadastrar, editar e consultar pessoas e seus endereços. O sistema expõe endpoints documentados no padrão OpenAPI (Swagger) e segue princípios REST, incluindo hypermedia com Spring HATEOAS para navegação entre recursos relacionados.

Cada pessoa pode possuir múltiplos endereços, com suporte a definição de endereço principal e operações de inclusão, edição, remoção e listagem. As regras de negócio ficam isoladas na camada de domínio (casos de uso e validadores), enquanto os controllers cuidam da validação de entrada e da delegação.

Para enriquecer o cadastro de endereços, a aplicação integra-se à API pública ViaCEP, permitindo buscar dados de logradouro a partir do CEP informado. A persistência é feita via Spring Data JPA, com banco H2 em memória no ambiente de desenvolvimento e MySQL (RDS) em produção.

O projeto inclui infraestrutura como código com AWS CDK para deploy em ECS Fargate, com Application Load Balancer e instância RDS MySQL. A aplicação é empacotada em container Docker e publicada para execução em cluster gerenciado na AWS.

## Stack

| Dependência | Versão |
|---|---|
| Java | 21 |
| Gradle | 8.7 |
| Spring Boot | 3.2.5 |
| Spring Data JPA | 3.2.5 |
| Spring HATEOAS | 3.2.5 |
| Spring Validation | 3.2.5 |
| Spring Boot Actuator | 3.3.0 |
| Springdoc OpenAPI | 2.5.0 |
| MapStruct | 1.5.5.Final |
| Gson | 2.10.1 |
| H2 Database | 2.2.224 |
| MySQL Connector | 8.0.33 |
| JUnit / Spring Boot Test | (via BOM Spring Boot) |
| Mockito | 5.23.0 |
| AWS CDK (módulo `cdk_project`) | 2.142.1 |

## Project structure

```
api-gerenciamento-pessoas/
├── cdk_project/                    # Infraestrutura AWS CDK (ECS Fargate, ALB, RDS MySQL)
│   └── src/main/java/com/myorg/    # Stack e app CDK
├── docs/                           # Documentação do projeto (diagramas de domínio)
├── gradle/wrapper/                 # Gradle Wrapper (distribuição 8.7)
├── src/
│   ├── main/
│   │   ├── java/.../gerenciamentopessoas/
│   │   │   ├── config/             # Configurações de bibliotecas (ex.: Gson)
│   │   │   ├── controller/         # REST controllers, DTOs, HATEOAS e tratamento de exceções HTTP
│   │   │   ├── domain/             # Entidades, casos de uso, repositórios, validadores e exceções de negócio
│   │   │   ├── integration/        # Integrações com serviços externos (ViaCEP)
│   │   │   └── mapper/             # Mapeamento entre entidades, DTOs e respostas de integração
│   │   └── resources/              # application.properties e perfis dev/prod
│   └── test/java/                  # Testes unitários (controllers, casos de uso, validadores, integração ViaCEP)
├── build.gradle                    # Build e dependências da aplicação Spring Boot
├── Dockerfile                      # Imagem Docker para deploy (JAR + Temurin 21)
└── README.md                       # Documentação principal do projeto
```

## External integrations

| Integração | Nome / URL | Descrição |
|---|---|---|
| Banco de dados (dev) | `jdbc:h2:mem:testdb` | Banco H2 em memória usado com o perfil `dev` para desenvolvimento local |
| Banco de dados (prod) | RDS MySQL `people_manager` | Persistência de pessoas e endereços em produção (provisionado via CDK) |
| API externa | `https://viacep.com.br/ws/` | Consulta de endereço por CEP via `ViaCepAddressFinder` |
| AWS ECS Fargate | `gerenciamento-pessoas-cluster` | Execução da aplicação containerizada em produção |
| AWS Application Load Balancer | (criado pelo CDK) | Roteamento de tráfego HTTP para as tasks ECS na porta 8080 |
| AWS RDS MySQL | `gerenciamento-pessoas` | Instância MySQL 8.0 provisionada pelo stack CDK |
| Container Registry | `thiagomarqs/gerenciamentopessoas:1.0.3` | Imagem Docker referenciada no deploy ECS |

---

# Commands

## Running the app

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

A aplicação sobe em `http://localhost:8080`.

- **Perfil `dev` obrigatório localmente:** as configurações de datasource (H2) estão em `application-dev.properties`; sem o perfil ativo, a aplicação não encontra banco configurado.
- **Swagger UI:** `/swagger-ui/index.html` (conforme README; path alternativo configurado em `application.properties`: `/swagger-ui.html`)
- **Console H2 (dev):** `/h2-console`
- **Health check (Actuator):** `/actuator/health`

Para produção, use o perfil `prod` com variáveis de ambiente de datasource (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`), conforme configurado no stack CDK.

## Running unit tests

```bash
./gradlew test
```

Relatório HTML gerado em `build/reports/tests/test/index.html`.

---

# Guidelines

## Logging

- Log at the right level: ERROR for failures requiring action, WARN for recoverable issues, INFO for significant business events, DEBUG for diagnostic detail.
- Include context in log messages: entity IDs, operation names — enough to trace with Observability tools.
- Use `StructuredArguments.kv(key, value)` for all log parameters — it adds the field to the JSON output and to the human-readable message. Never use string concatenation.
    - Use `kv()` as the default.
    - Use `v()` when the key is already in the message text and repeating it would be redundant.
    - Use `e()` or `f()` to log all entries from a Map or object fields at once.
    - Example: `log.info("Pedido criado", kv("id", id))` not `log.info("Pedido criado id={}", id)`.
- Don't log inside loops — aggregate and log once after.
- Don't log what Spring already logs (startup, request mapping, transaction boundaries) — check before adding.

## Coding

- Controllers own validation and delegation. Services own business logic. Repositories own persistence. Don't cross the line.
- Use comments sparingly. Prefer writing comments only for complex code.
- Prefer `Optional` over null returns in service/repository layers.
- Use `@Value` for config — never hardcode env-specific values.
- Never hardcode credentials, API keys, or secrets — use environment variables or a secrets manager.

## Exception Handling

- When catching and rethrowing, always add context: `throw new OrderProcessingException("Failed to process orderId=" + id, e)` — never rethrow without wrapping or logging.
- Never log and rethrow the same exception — it causes duplicate stack traces. Log once at the handler, or rethrow and let the boundary log it.
- Always preserve the original cause when wrapping exceptions — never discard the root cause.


## Testing

### Unit Tests

- One scenario per method. AAA structure: Arrange / Act / Assert.
- Test behavior, not implementation (no `verify` unless interaction IS the behavior).
- Follow the test naming style used by existing tests.
- Tests must be isolated and independent. No shared state.
- Create reusable test fixtures for objects the tests run against. Use the factory pattern for that.
- Stub only what the test needs — no over-mocking.
- Use parameterized tests to group similar tests into a single method to avoid code repetition.
- Prefer following a black-box approach: prefer verifying behavior, not internal structure. This helps avoid coupling with production code.
- Before creating a new test, verify if there's an existing one that already satisfies the scenario or if it can be incremented or updated. This helps avoid uncontrolled growth of test code.

### Integration Tests

- Integration tests verify if the application works in integration with its external integrations (database, APIs, queues, topics, etc...).
- Must follow a black-box approach: verify behavior and the side effects produced by the application (items created in the database, messages posted to queues or topics, API response, etc...)
- Prefer covering paths and edge cases unit tests can't.
- Tag each scenario for selective execution. Example: @CadastrarPessoaComSucesso
- Each scenario must clean the data it created before the next one runs.
- Each scenario must be independent, idempotent and runnable in isolation in any order — no shared mutable state between scenarios.
- Each scenario tests one behavior. If the title needs "and", split it.
- Use Scenario Outline for the same behavior with multiple data variations — not for unrelated cases.
- Given steps describe state, When steps describe the action, Then steps describe the observable outcome. Never mix them.
- Reuse existing step definitions before writing new ones — check existing steps first.
- When a scenario requires specific data, create it in the Given steps — never rely on pre-existing DB state.
- Testcontainers manages all external dependencies (DB, messaging, etc.) — no mocks for infra.
- Keep integration tests in a separate Maven profile (`integration-tests`) to avoid slowing down the default build.

---

# Constraints

## Always

- Use Brazilian Portuguese as the primary language for documentation, comments and commits.
- ALWAYS create branches from the `origin/develop` branch. Never from `main`.
- ALWAYS ensure branches are updated before working (`git fetch` to check and `git pull` to update.)
- Branch naming convention: `feature/{feature-name}`.
  - `{feature-name}` should be a descriptive and concise name summarizing the feature's intent.
  - Example: `feature/setup-testes-integracao`, `feature/mapeamento-campos` or `feature/remocao-token-validation`.
- Follow existing module and package structure — don't reorganize without asking.
- Reference the project's `build.gradle` when running Gradle commands to ensure correct configuration is being used.

## Never

- Push directly to `main` or `develop`.
- Catch and swallow exceptions silently.
- Commit secrets, credentials, or environment-specific URLs.
- Use `System.out.println` for logging — use the project's logging dependency.

## Ask First

- Large cross-package refactors.
- New dependencies.
- Destructive data or migration changes.
- Modifying shared configuration classes, files or `@Bean` definitions.
- You need information that isn't in this file or the codebase to proceed confidently.
- A requirement is ambiguous and different interpretations would lead to different implementations.
- Assumptions that affect design or behavior — state it and confirm.

---

# Review checklist

[ ] Re-read the original request and verify each requirement is addressed — nothing skipped, nothing added beyond scope.
[ ] New behavior has test coverage.
[ ] ALL Unit tests passing: `./gradlew test`
[ ] Unit test branch coverage of at least 90% in updated classes (run Jacoco and check its coverage report for the classes you updated).
[ ] No unused imports, dead code, or commented-out code created by you left behind.
[ ] New dependencies added to `build.gradle` were flagged and approved (see Ask First).
