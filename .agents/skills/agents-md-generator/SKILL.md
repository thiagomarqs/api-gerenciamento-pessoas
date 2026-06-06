---
name: agents-md-generator
description: >
  Generates a populated AGENTS.md file at the root of a Java/Spring Boot project by analysing
  the codebase and filling in all placeholders. Use this skill whenever the user asks to
  generate, create or initialise an AGENTS.md (or CLAUDE.md) for a project, even if they
  just say "set up the project memory file" or "create the agents file".
---

# AGENTS.md Generator

Generates a populated `AGENTS.md` at the project root by analysing the codebase.
The template lives at `assets/TEMPLATE.md`. Read it before starting.

---

## Phase 1 — Orient

Before analysing, establish the project root:
- If the user specified a path, use it.
- Otherwise, use the current working directory.
- Confirm the path exists and contains a `pom.xml`. If not, ask the user.

Check if `AGENTS.md` already exists at the root. If it does, **stop and ask** before overwriting.

---

## Phase 2 — Analyse

Read the following files in order. Extract only what is needed to fill the placeholders.
Do not run Maven or execute the application.

### 2.1 — Build & Stack (`pom.xml`, `settings.xml`)
- Parent artifact: Spring Boot version.
- Java version (`maven.compiler.source` or `<java.version>`).
- Key dependencies and their versions: Spring Boot, Spring Data, Testcontainers, Cucumber, Liquibase/Flyway, Lombok, logging libs, HTTP clients, messaging clients, etc.
- Maven profiles: identify the default build profile and any `integration-tests` profile.
- Construct commands:
  - Read `README.md` if present and check for extra information on running the app.
  - Run app: `mvn spring-boot:run` + active profile flag if needed + `-s settings.xml` if `settings.xml` is present.
  - Unit tests: `mvn test` + `-s settings.xml` if needed.
  - Integration tests: only if an `integration-tests` profile exists in `pom.xml`.
- Reference both `pom.xml` and `settings.xml` in every Maven command if `settings.xml` is present.

### 2.2 — Project Structure (folder tree)
- Walk the project root up to 3 levels deep.
- Skip: `.git`, `target`, `.idea`, `.mvn`, `*.class`, `*.jar`.
- Record each meaningful folder with a one-line description.

### 2.3 — External Integrations
Scan the following for datasource URLs, queue/topic names, API base URLs, and cloud resource names:
- `src/main/resources/application*.yml` and `application*.properties`
- Any Spring `@Configuration` classes that declare beans for DataSource, RestTemplate, WebClient, JmsTemplate, KafkaTemplate, or similar.
- Infrastructure-as-code files: `*.tf` (Terraform), `*.yaml`/`*.json` CloudFormation templates.
- For each integration found, record: type (DB, SQS, Kafka, API, etc.), name or URL, one-line description of how it is used.
- If purpose cannot be confidently inferred, mark it `[REVIEW: purpose unclear]`.

### 2.4 — Business Summary
- Read `README.md` if present.
- Scan top-level package names and domain class names (`src/main/java`) for business vocabulary.
- Produce a plain-language summary (up to 5 paragraphs) understandable by non-technical readers.
- If context is insufficient for a confident summary, write what can be inferred and mark gaps with `[REVIEW: add business context]`.

---

## Phase 3 — Fill the Template

Read `assets/TEMPLATE.md`. For each `FILL:` placeholder, replace it with the information gathered in Phase 2 using the rules below.

| Placeholder | Fill rule |
|---|---|
| `Summary` | Business summary from 2.4. Mark gaps with `[REVIEW]`. |
| `Stack` | Dependency table from 2.1 (dependency, version). |
| `Project structure` | Folder tree from 2.2. Inline `#` comments per folder. |
| `External integrations` | Integration table from 2.3 (type, name/URL, description). |
| `Running the app` | Command from 2.1. Add warnings if profiles or env vars are required. |
| `Running unit tests` | Command from 2.1. |
| `Running integration tests` | Command from 2.1. **Omit this entire section** if no integration-tests profile is found. Add warnings if profiles or env vars are required. |
| `Review checklist commands` | Replace with actual Maven commands. |

### Stripping rules
Before writing the output, remove:
- All `FILL:` markers.
- All formatting instructions in parentheses, e.g. `Format: Table (...)`.
- The `(Optional, include only if the app has integration tests)` annotation from the section header.
- Any section whose content is entirely `[REVIEW]` markers and cannot be partially filled — keep it but mark it clearly.

### `[REVIEW]` marker format
Use `[REVIEW: <one-line reason>]` inline where a value could not be confidently inferred.
Never silently omit a placeholder. Never guess at business meaning or integration purpose.

---

## Phase 4 — Write Output

- Write the populated file to `<project-root>/AGENTS.md`.
- After writing, print a summary of every `[REVIEW]` marker left in the file so the developer knows exactly what needs manual attention.

---

## Edge Cases

- **Multi-module Maven project**: use the root `pom.xml` for stack/version info; walk each module's `src/` for integrations and structure.
- **No `settings.xml`**: omit `-s settings.xml` from all commands.
- **No `README.md` and sparse package names**: mark the entire Summary as `[REVIEW: business context could not be inferred — please describe the application]`.
- **Multiple `application-*.yml` profiles**: note the active profile in the run command and list env-specific integrations under the profile name.
