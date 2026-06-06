---
name: sdd-plan
description: Third step of Spec-Driven Development. Use this skill to create an implementation plan from a spec. Triggers on phrases like "cria o plano", "planeja a implementação", "quebra em tarefas", "create the plan", "break this into tasks", or whenever the user wants to translate a spec into actionable implementation steps. Always read the spec.md before planning. Use this skill even for medium-sized features — the parallelism analysis alone is worth it.
---

# SDD Plan

This skill produces a `plan.md` — a structured implementation plan derived from the spec. Its job is to break the feature into well-scoped tasks that an AI agent (or human developer) can execute one at a time, with clear parallelism opportunities identified for subagent execution.

The plan is about *how* to build what the spec describes. It should be technical, but not prescriptive about every detail — leave room for the implementer to make sensible decisions within each task.

## Before writing

### Read the spec
Look for `docs/sdd/{feature-slug}/spec.md`. If it doesn't exist, stop and tell the user:
> "Não encontrei uma spec.md para essa feature. O plano depende da spec para ser preciso. Quer que eu crie a spec primeiro?"

Don't proceed without a spec.

### Understand before planning
Read the spec fully. Identify:
- The user stories and their acceptance criteria
- Any technical constraints mentioned
- The scope boundaries (especially "Out of Scope")

If there are open questions in the spec that would meaningfully affect the plan, flag them before proceeding.

## Writing the plan

Save to `docs/sdd/{feature-slug}/plan.md`.

Use this template exactly:

---

```markdown
# Plan: {Feature Name}

## Technical Approach
2–4 sentences describing the overall implementation strategy.
Mention major architectural decisions, key components, and how they relate.
This is the "shape" of the solution before the details.

## Dependency Graph
Visual or textual map of which tasks depend on which.
Tasks with no dependencies can run in parallel.

Example:
```
[1] Setup DB schema
[2] Create API endpoint  ← depends on [1]
[3] Write unit tests     ← depends on [2]
[4] Update UI component  ← depends on [1]
[5] Integration tests    ← depends on [3, 4]
```

## Execution Waves
Group tasks by when they can execute. Tasks in the same wave have no dependencies between them and can run in parallel.

### Wave 1 (parallel)
- Task [N], Task [N]

### Wave 2 (parallel)
- Task [N], Task [N]

### Wave 3 (sequential)
- Task [N]

## Tasks

### Task 1: {Title}
**Complexity:** simple | medium | complex  
**Depends on:** none  
**Parallelizable with:** [list task IDs, or "none"]  
**Files affected:** list of files to create or modify  

Description of what this task does and why. Include enough detail for the implementer to understand the goal, but don't write the code.

**Done when:**
- [ ] {Verifiable condition}
- [ ] {Verifiable condition}

---

### Task 2: {Title}
...
```

---

## Task sizing

A well-sized task:
- Can be completed in a single agent context window
- Has a clear, observable output
- Touches a bounded set of files

If a task feels too large (e.g., "Implement the entire API layer"), split it. If it feels too small (e.g., "Add one import"), merge it with related work.

Flag tasks marked as `complex` with a note explaining what makes them hard — these may need extra attention during implementation.

## Parallelism guidelines

Tasks are parallelizable when they:
- Operate on **different files** with no shared state
- Don't depend on each other's output
- Don't make conflicting assumptions (e.g., two tasks that both define the same interface differently)

Tasks are **not** parallelizable when:
- One writes a contract (interface, schema, API) that the other consumes
- They touch the same file
- A decision in one affects the implementation of the other

When in doubt, make tasks sequential. Parallelism is an optimization — correctness comes first.

## Traceability

Each task should map to one or more acceptance criteria from the spec. If a task doesn't map to any criterion, question whether it's necessary. If an acceptance criterion has no task covering it, the plan is incomplete — fix this before finishing.

You don't need to add explicit "maps to criterion X" annotations unless it's unclear — the task descriptions should make the mapping obvious.

## Iterating on the plan

If the user says "adiciona uma tarefa" or "muda a abordagem de X", update in place and add to the changelog:

```markdown
## Changelog
- {date}: {brief description of change}
```

## Handoff

When done:
- Confirm where the file was saved
- State the total number of tasks, waves, and how many tasks are parallelizable
- Flag any `complex` tasks that may need extra attention
- Suggest running `sdd-implement` next, passing both `spec.md` and `plan.md`
