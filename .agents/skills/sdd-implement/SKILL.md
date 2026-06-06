---
name: sdd-implement
description: Fourth step of Spec-Driven Development. Use this skill to execute an implementation plan and produce working code. Triggers on phrases like "implementa", "executa o plano", "começa a implementação", "implement the plan", "start coding", or whenever the user wants to turn a plan.md into actual code. Always read both spec.md and plan.md before starting. Use this skill for any feature implementation that has a plan — it handles both sequential execution and parallel subagent dispatch for independent tasks.
---

# SDD Implement

This skill executes the `plan.md` and produces working code, validating each task against the `spec.md` acceptance criteria as it goes. It's the only step that writes code. Everything before this was preparation — now we build.

## Before starting

### Read both documents
Always load:
1. `docs/sdd/{feature-slug}/spec.md` — the source of truth for what's correct
2. `docs/sdd/{feature-slug}/plan.md` — the source of truth for what to build and in what order

If either is missing, stop:
> "Preciso da spec.md e do plan.md para implementar. Qual deles está faltando?"

### Understand the waves
Look at the Execution Waves section of `plan.md`. This determines your execution strategy:
- **Single task in a wave** → execute directly
- **Multiple tasks in a wave** → dispatch as parallel subagents (see below)

## Execution

### Sequential tasks
Execute one task at a time, in wave order. For each task:

1. Read the task description and "Done when" criteria
2. Implement the changes
3. Validate against the task's "Done when" criteria
4. Validate against any related acceptance criteria in `spec.md`
5. Update `plan.md` — mark the task as complete:
   ```markdown
   ### Task N: {Title} ✅
   **Status:** complete
   ```
6. If validation fails, fix before moving to the next task — don't carry broken state forward

### Parallel tasks (subagents)
When a wave contains multiple parallelizable tasks, dispatch them as subagents in the same turn.

Each subagent receives:
```
Execute this implementation task as part of a Spec-Driven Development workflow.

Spec: docs/sdd/{feature-slug}/spec.md
Plan: docs/sdd/{feature-slug}/plan.md
Task to implement: Task {N} — {Title}

Read the spec and plan before starting.
When done, report:
- Files created or modified
- Whether "Done when" criteria were met
- Any blockers or deviations from the plan
```

After all subagents in the wave complete:
- Review their outputs for conflicts (e.g., two tasks that modified the same file in incompatible ways)
- Resolve any conflicts before starting the next wave
- Update `plan.md` with the status of all completed tasks

### When something goes wrong

If during implementation you encounter a blocker — the plan is wrong, a dependency is missing, or the spec is ambiguous — **stop and report** rather than improvising:

> "Encontrei um problema na Tarefa N: {descrição do problema}. Isso afeta as tarefas {lista}. Quer que eu atualize o plano antes de continuar?"

Don't silently deviate from the plan. Deviations that aren't discussed become tech debt.

## Validation

After completing all tasks, do a final validation pass against the spec:

1. Go through every acceptance criterion in `spec.md`
2. For each criterion, verify it's met by the implementation
3. Report results:

```markdown
## Validation Report

### ✅ Passing
- [Criterion]: implemented in {file/task}

### ❌ Failing
- [Criterion]: not met — {reason}

### ⚠️ Unverifiable
- [Criterion]: requires manual or integration testing
```

If any criteria are failing, either fix them or explicitly flag them for the user to decide.

## Updating plan.md as a progress log

The `plan.md` serves as a live progress document during implementation. Keep it updated:

- Mark tasks with ✅ when complete
- Add `**Status:** blocked — {reason}` for tasks that couldn't be completed
- Add a final section when all tasks are done:

```markdown
## Implementation Summary
- Completed: N/N tasks
- Started: {date}
- Finished: {date}
- Deviations from plan: {list, or "none"}
```

## Handoff

When implementation is done:
- Share the validation report
- List any open items (failing criteria, unverifiable criteria, deviations)
- Suggest next steps: code review, manual testing, or updating the spec if scope changed
