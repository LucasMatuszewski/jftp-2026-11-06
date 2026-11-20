---
name: write-implementation-plan
description: Draft or revise a repository implementation plan as Markdown in normal mode, especially for multi-step changes that need agent delegation, preflight review, or staged verification. Use when asked for a plan file, an implementation roadmap, or edits to an existing plan.
---

# Write an implementation plan

## Plan in the current mode

- Write the plan directly in the current collaboration mode. Default to `DocsPlans/<descriptive-kebab-case>.md` at the repository root. Honor a requested path, and edit an existing plan in place unless asked to move it.
- Treat a plan request as authorization to explore and write planning documents. Start implementation only when the user asks for it. Mark the plan's readiness, identify open decisions, and give the user the exact path. Follow the repository's documentation verification and commit rules.
- Involve the user when choices materially change scope, behavior, architecture, or acceptance. Gather evidence first, present the practical options and tradeoffs, and record the chosen option in the plan. Resolve routine decisions from the repository and state material assumptions.

## Establish and review the baseline

- Compare applicable root and nested `AGENTS.md`, the product baseline (PRD or PLD if present), ADRs, architecture and design descriptions, and the current code, configuration, and tests. Use repository indexes and scoped reads. Record the starting commit and any relevant uncommitted work.
- Record inconsistencies with evidence and distinguish historical snapshots from current requirements. Apply repository rules to workflow, product requirements to behavior, and accepted ADRs to architecture. Resolve verified drift or assign the correction. Leave affected work blocked when a consequential conflict needs a decision.
- Request an independent preflight review for substantial plans when delegation is available. Give the reviewer a brief, ask for evidence of contradictions, ambiguous tasks, missing scope, or untestable gates, and have the lead resolve findings before implementation starts.

## Specify the execution

- Define the intended outcome, scope, preserved behavior, verified prerequisites, dependencies between work items, ownership, allowed paths, deliverables, and measurable acceptance. Name the actual build, test, startup, and manual QA checks where applicable. Identify the evidence and the action if a gate fails.
- For modernization of an existing application, characterize observable behavior before edits. Restore startup with the smallest necessary change, run the baseline tests and applicable real-app checks, and commit verified logical changes before wider upgrades or refactoring. Keep behavior tests independent of implementation details. Investigate a new failure as an application or build regression first. Change test expectations only for an agreed behavior change.
- During implementation the lead delegates research, code, tests, QA, and scoped commits. The lead coordinates ownership and dependencies, keeps the user informed at meaningful gates, checks diffs and evidence, authorizes commits after verification, and checks their exact contents. Use Luna for exploration and read-only research or routine QA, Sol for coding and deeper QA, and Astra only for exceptionally difficult tasks with a stated reason.
- Default to one checkout with disjoint write paths. Serialize shared-file edits. Use a worktree only when overlapping writes genuinely must run in parallel, with an owner and integration check. Run concurrent QA against a named commit or frozen snapshot with isolated outputs and state, then repeat the gate on the integrated result.

## Delegate through temporary briefs

- Before dispatch, write one Markdown brief per assignment. Use `docs/.briefs/<plan-name>/<task-id>.md` for plan work, and record `Plan: <repo-relative plan path>` in the brief. Use `docs/.briefs/standalone/<task-id>.md` and `Plan: none` for independent work. Use a unique plan-name directory and reserve standalone for independent work. Create directories on demand.
- State the task ID, objective, baseline, inputs, the allowed read and write paths and actions, required verification, expected output or evidence, and when to report a blocker. Give the worker an accessible path and a short handoff such as “Follow the instructions in this brief and report the requested evidence.” Keep the full assignment in the file.
- Use the same handoff for native subagents and CLI agents. For authorized external exploration with OpenCode and Xiaomi MiMo-V2.6-Pro, see [headless OpenCode](references/opencode.md).
- Version briefs in Git. Include initial plan briefs in the plan commit and commit later briefs as scoped changes for the same plan. Commit standalone briefs with their related work. Keep credentials and private test data out of tracked briefs.
- After completing the plan and passing its final gate, verify each brief's explicit plan link and remove the matching briefs in the plan's closing commit. When retiring a cancelled plan, remove it and its linked briefs together. Preserve other plans' briefs. Remove standalone briefs when their own assignments close.
