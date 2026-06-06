# Project Overview

## Summary
FILL: Overview of the application, from the business point-of-view. The text must be understandable by technical and non-technical people. Up to 5 paragraphs.

## Stack
FILL: Short summary of the project's stack in table format (dependency, version)

## Project structure
FILL: The project's folder tree and short description of each folder. Format: Folder tree. Description as comments marked with # on the same line as the folder.

## External integrations
FILL: Brief overview of external integrations the application interacts with (e.g.: DynamoDB tables, SQS queues, Kafka topics, APIs, etc... in table format (integration, name or URL, brief description of how it's used).

---

# Commands

## Running the app
FILL: Commands to run the app. Include any relevant tips or warnings if needed.

## Running unit tests
FILL: Commands to run the unit tests. Include any relevant tips or warnings if needed.

## Running integration tests (Optional, include only if the app has integration tests)
FILL: Commands to run the integration tests. Include any relevant tips or warnings if needed.

---

# Rules
- Use Brazilian Portuguese as the primary language for documentation, comments and commits.
- ALWAYS create branches from the `origin/develop` branch. Never from `main`.
- ALWAYS ensure branches are updated before working (`git fetch` to check and `git pull` to update.)
- Branch naming convention: `feature/{feature-name}`.
    - `{feature-name}` should be a descriptive and concise name summarizing the feature's intent.
    - Example: `feature/setup-testes-integracao`, `feature/mapeamento-campos` or `feature/remocao-token-validation`.
- Follow existing module and package structure — don't reorganize without asking.
- Reference the project's `pom.xml` and `settings.xml` files when running Maven commands to ensure correct configuration is being used.

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

- [ ] Re-read the original request and verify each requirement is addressed — nothing skipped, nothing added beyond scope.
- [ ] New behavior has test coverage.
- [ ] ALL Unit tests passing: `FILL: Include the unit tests command`
- [ ] ALL Integration tests passing: `FILL: Include the integration tests command` (Include only if the app has integration tests)
- [ ] Unit test branch coverage of at least 90% in updated classes (run Jacoco and check its coverage report for the classes you updated).
- [ ] No unused imports, dead code, or commented-out code created by you left behind.
- [ ] New dependencies added to pom.xml were flagged and approved (see Ask First).
