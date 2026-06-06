---
name: sdd-research
description: First step of Spec-Driven Development. Use this skill when the user wants to start developing a new feature, investigate the codebase before writing a spec, or gather context for a new functionality. Triggers on phrases like "quero implementar X", "preciso desenvolver X", "vamos começar uma feature", "pesquisa sobre X no projeto", "analyze the codebase for X", or any time the user describes a feature they want to build and hasn't written a spec yet. Always use this skill before sdd-spec — it feeds the context that makes the spec accurate.
---

# SDD Research

This skill guides the research phase of Spec-Driven Development. The goal is to gather enough context from the existing codebase so that the next step (spec writing) is grounded in reality — not assumptions.

Research is about reading and understanding, not writing code. Resist the urge to suggest solutions at this stage.

## What to do

### 1. Understand the feature intent

Ask the user (if not already clear):
- What does this feature do from a user's perspective?
- Is there an existing feature it extends or replaces?
- Any known constraints or decisions already made?

Keep this brief — one or two questions max. Don't over-interview before doing the work.

### 2. Explore the codebase

Use `find`, `grep`, `cat`, and `ls` to explore. Focus on:

- **Entry points** related to the feature domain (routes, controllers, handlers, pages)
- **Data models** that the feature will touch or need
- **Existing patterns** — how similar features are structured in this codebase (naming, layers, file organization)
- **Dependencies** — external libraries, internal services, shared utilities already in use
- **Tests** — what testing patterns exist, where tests live

Prioritize breadth over depth. You want a map, not a deep-dive into every file.

### 3. Identify constraints and risks

Look for things that could affect implementation:
- Parts of the code that are fragile, undocumented, or have many dependents
- Conflicting patterns (e.g., two different ways of doing auth)
- Missing abstractions the feature might need
- Open questions that require product or business input (flag these — don't assume answers)

### 4. Write the research document

Save to `docs/sdd/{feature-slug}/research.md`. Create the directory if it doesn't exist.

Use this template exactly:

---

```markdown
# Research: {Feature Name}

## Feature Intent
Brief description of what was understood about the feature from the user.

## Codebase Context

### Relevant Entry Points
List files/routes/handlers directly related to this feature's domain.

### Data Models
List existing models/schemas/tables the feature will likely touch.

### Existing Patterns
Describe how similar things are done in the codebase. Include file path examples.

### Dependencies
List relevant libraries, services, or utilities already available.

### Test Patterns
Describe how tests are structured and where they live.

## Constraints & Risks
- [Constraint or risk]: [brief explanation]
- ...

## Open Questions
Questions that need product, business, or technical decisions before or during spec writing.
- ...

## Files Explored
List the main files read during this research (for traceability).
```

---

## What not to do

- Don't propose a solution or architecture — that's for the plan step.
- Don't write any code.
- Don't include everything you read — be selective. The document should be useful to someone writing a spec, not a dump of `cat` outputs.
- Don't leave Open Questions empty if there are genuine ambiguities. Better to surface them now than have the spec make wrong assumptions.

## Handoff

When done, tell the user:
- Where the file was saved
- How many open questions were identified (if any)
- Suggest running `sdd-spec` next, passing `docs/sdd/{feature-slug}/research.md` as input
