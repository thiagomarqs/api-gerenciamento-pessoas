---
name: sdd-spec
description: Second step of Spec-Driven Development. Use this skill to write or update a feature specification (spec). Triggers on phrases like "escreve a spec", "cria a especificação", "define os requisitos de", "write the spec for", "update the spec", or whenever the user wants to formalize what a feature should do before implementation. Always check for a research.md before proceeding — the spec is only as good as the context behind it. Use this skill even if the user says "just write a quick spec" — the structure is what makes it useful.
---

# SDD Spec

This skill produces a `spec.md` — a product-focused specification written in natural language. It's the source of truth for what gets built. Developers, AI agents, and reviewers should be able to read this document and understand exactly what the feature does and how to know when it's done.

The spec is about *what* and *why*, not *how*. Architecture and implementation details belong in the plan step.

## Before writing

### Check for research
Look for `docs/sdd/{feature-slug}/research.md`. If it exists, read it fully before proceeding — it provides critical context.

If it doesn't exist, ask the user:
> "Não encontrei um research.md para essa feature. Você quer que eu faça a etapa de pesquisa primeiro, ou prefere fornecer o contexto manualmente?"

Don't refuse to proceed if the user wants to skip research — but note in the spec that it was written without a research step.

### Clarify ambiguities before writing
Identify the top 2–3 things that are genuinely unclear from the user's input and the research (if available). Ask them upfront in a single message. Don't ask for information you can reasonably infer.

Things worth clarifying:
- Who are the users of this feature? (roles, personas)
- What's the primary success scenario?
- Any explicit non-goals or things explicitly out of scope?
- Edge cases with meaningful business impact

Once you have answers, write the spec without further interruption.

## Writing the spec

Save to `docs/sdd/{feature-slug}/spec.md`.

Use this template exactly:

---

```markdown
# Spec: {Feature Name}

## Introduction
One paragraph describing the feature, its purpose, and why it's being built.
Context from research and user input goes here.

## Users & Context
Who uses this feature and in what situation.
Example: "Usuários autenticados com papel de administrador, acessando o painel de configurações."

## User Stories

### Story 1: {Short title}
**Como** {persona},  
**quero** {ação},  
**para** {objetivo/valor}.

### Story 2: {Short title}
...

## Functional Requirements
Numbered list of what the system must do.
1. ...
2. ...

## Non-Functional Requirements
Include only what's genuinely relevant. Omit this section if there's nothing meaningful to say.
- Performance: ...
- Security: ...
- Accessibility: ...

## Acceptance Criteria

### Story 1: {Short title}
- [ ] {Specific, testable condition}
- [ ] {Specific, testable condition}

### Story 2: {Short title}
- [ ] ...

## Out of Scope
Explicit list of things this spec does NOT cover. This prevents scope creep and misinterpretation.
- ...

## Open Questions
Any unresolved questions that surfaced during spec writing. These should be resolved before or during planning.
- ...
```

---

## What makes good acceptance criteria

Acceptance criteria are the most important part of the spec — they're how an AI agent (and a human reviewer) validates that the implementation is correct.

Good criteria are:
- **Specific**: "O botão de submit fica desabilitado enquanto a requisição está pendente" — not "O UX é bom"
- **Testable**: Someone (or an agent) can verify it without interpretation
- **Grounded in the user story**: Each criterion maps to a story

Weak criteria to avoid:
- "O sistema funciona corretamente"
- "A performance é aceitável"
- "O código é limpo"

Every user story must have at least one acceptance criterion. Don't finalize the spec without them.

## Iterating on the spec

The spec can and should be updated. If the user says "adiciona um requisito" or "muda essa story", update the file in place and note what changed at the bottom:

```markdown
## Changelog
- {date}: {brief description of change}
```

## Handoff

When done:
- Confirm where the file was saved
- Summarize the stories and how many acceptance criteria were defined
- Flag any open questions that need resolution
- Suggest running `sdd-plan` next, passing `docs/sdd/{feature-slug}/spec.md` as input
