# Restore jFTP startup, then modernize to Java 25 LTS

## Sources of truth

- [Product requirements and acceptance criteria](PRD.md#6-acceptance-criteria) define the behavior to preserve.
- [Current-state ADR](ADR/000-main-architecture.md), [architecture diagrams](architecture.md), and the [resource inventory](jftp-zasoby.md) describe the existing technical contracts. Use the POM, source, and test suite for facts about the revision being changed.
- The [repository workflow](../AGENTS.md#agent-workflow) governs implementation and verification. This plan defines migration decisions, work order, and completion gates.

## Preflight

The lead performs preflight before stage 1 and repeats affected checks when the sources change:

- [ ] Read the sources of truth, applicable nested instructions, and any relevant ADRs. Reconcile their revisions and scope before deriving acceptance criteria for a brief. Use the [current test suite](../src/test/README.md) as evidence for the behavioral baseline.
- [ ] Record the starting revision and changes in the working tree. Reconcile any existing startup work with the stage 1 gate and protect unrelated changes. Give the [preflight worker](.briefs/modernization-plan/00-preflight.md) the facts to check against the repository.
- [ ] Resolve verified documentation drift with evidence. Bring consequential conflicts in behavior, architecture, or verification to the user before starting the affected work.

## Execution and delegation

- The lead agent delegates research, implementation, test execution, and QA. It defines scope, acceptance criteria, dependencies, and file ownership, then reviews diffs and evidence, coordinates integration, and verifies gates. For each verified logical change, the lead authorizes one responsible worker to stage the agreed paths and create the local commit, then checks its exact contents.
- Use **Luna** for research, read-only analysis, and QA by default. Use **Sol** for code and for analysis or QA that requires deeper domain knowledge. Use **Astra** only for an exceptionally difficult problem and record why it is needed.
- Run independent assignments in parallel in one checkout. Give each writer explicit, disjoint paths, including test files and generated artifacts. Assign shared files to one writer at a time. If concurrent writes to overlapping paths are unavoidable, document why a worktree is needed, assign its owner, and have the lead agent integrate and verify the result.
- Overlap QA with implementation only when the tested commit or frozen snapshot is identified and the outputs are isolated. Give each concurrent run its own build directory, extracted package, synthetic preferences, test-server state, logs, and screenshots as applicable. Run the relevant gate again on the integrated candidate before committing.
- Keep the user informed at stage gates with the outcome, verification evidence, and commit IDs. Ask for a decision when scope, behavior, or architecture would change, and surface gates that remain blocked after investigation with the evidence and the decision or prerequisite needed to resume.

### Temporary task briefs

- The lead reads the source documents and resolves material choices before writing each brief in `docs/.briefs/modernization-plan/`. Include the `Plan` link, task ID, revision or snapshot, agreed behavior, exact inputs and paths, actions in order, commands, expected results, and the report format. Include the relevant workflow rules in the brief. The worker should be able to complete the assignment from the brief and its named inputs. For Luna, provide an explicit checklist and stop conditions for common failures. Escalate any missing prerequisite, conflict, or material choice to the lead.
- Send the internal agent or subagent only the brief path and a short instruction to follow it and report evidence. For a headless CLI agent, use the same handoff with a copy of the brief in its working directory. Check that the worker can access the brief before starting.
- Keep the initial briefs with the plan commit. During implementation, commit each new or revised brief as soon as the lead has checked it and before handing it to a worker. Keep credentials and private test data out of tracked briefs. Assign independent work under `docs/.briefs/standalone/` and record `Plan: none`.
- After completing the plan and passing its final gate, check each brief's `Plan` field and remove briefs linked to this plan in the closing commit. Remove the links to retired briefs if this plan stays in the repository. Apply the same cleanup if this plan is cancelled. Preserve other plans' and standalone briefs.

## 1. Establish a working baseline

- Use the [baseline test brief](.briefs/modernization-plan/01-verify-baseline.md) to establish the result for all 11 existing cases and preserve their assertions.
- Make the smallest build changes needed for `mvn clean verify`: use the verified jMethods HTTPS repository, set compiler release 8, add compile-only AppleJavaExtensions 1.4, and pin Surefire 3.5.5 for JUnit 3. Keep the rest of the runtime dependency set and change application code only for a reproduced startup failure.
- Align both launchers with the [assembly contract](ADR/000-main-architecture.md#3-system-architecture-and-distribution). On Windows, launch the extracted package from another working directory with spaces in the package path. Verify packaged startup against PRD `AC-01`, exercise local browsing and bundled help, and confirm clean exit.

**Gate.** The package builds, the real window opens, all 11 behavioral tests pass, and the applicable verification checklist below is complete. Commit the build restoration, launcher fix, and any necessary application fix as separate verified changes. Then continue into modernization.

## 2. Modernize in independently verified commits

1. **Retire obsolete entry points.** Remove the browser applet and its applet-specific constructor and exit branch. Remove the unreferenced `OSXAdapterOld` and the compile-only Apple dependency. Preserve the packaged desktop entry point and the active macOS integration.
2. **Move to Java 25 LTS.** Require JDK 25 and compiler release 25. Upgrade build and packaging plugins, including Maven JAR 3.5.1 and Assembly 3.8.0. Preserve the packaging contract verified in stage 1.
3. **Upgrade the test runner.** Port the 11 existing cases to JUnit Jupiter 6.1.3 with Surefire 3.6.1 in a dedicated commit. Preserve discovery, inputs, assertions, and failure conditions. Repeat the [documented popup regression check](../src/test/README.md#characterization-and-regression-tests).
4. **Refactor by behavior area.** Modernize collections and boxing in profiles, local-file handling, and session code. Use try-with-resources in ZIP and persistence code while preserving close order, progress callbacks, and error behavior. Retain the existing `ftpapi` and JavaHelp versions and preserve the [architecture and compatibility contracts](ADR/000-main-architecture.md#8-recorded-technical-decisions), including serialized field names, types, and `serialVersionUID` values.

**Gate.** The packaged app launches on Java 25 on Windows, all 11 migrated tests pass with their baseline assertions, and the loopback FTP/FTPS and preference and favorite reload checks pass. Complete the verification checklist and record the verified local commits.

## 3. Protect behavior and verify each change

- Treat a newly failing test as a regression in the application or build first. Reproduce it on the last green commit, compare observable behavior, fix the changed code, and rerun the affected test and full suite. Changes to tests during the JUnit migration are limited to framework mechanics. Any change to expected behavior requires separate agreement.
- Before changing transfer or persistence code, record the relevant [product flows and acceptance criteria](PRD.md#4-main-flows) against a real loopback FTP/FTPS server on Windows. Include login refusal, binary byte equality, a ZIP round trip, abort before the next batch item, and a second session tab in the verification brief. Save synthetic preferences and favorites on the baseline and verify that Java 25 reloads them.
- For each runtime-impacting commit, run `mvn clean verify` and follow repository [Manual QA](../AGENTS.md#manual-qa) on Windows. Report native macOS and Linux coverage separately.
- Update the build and test instructions with the verified commands, refresh affected source-derived docs, and keep each verified logical change in its own local commit.

### Verification checklist for both stage exits

- [ ] Review the integrated diff and test evidence. Confirm each logical change passed its required checks before the lead authorized its assigned worker to commit it, then verify the resulting commit contents.
- [ ] Complete the relevant [TDD](../AGENTS.md#tdd-rules), [test strategy](../AGENTS.md#test-strategy), [Manual QA](../AGENTS.md#manual-qa), and [commit checks](../AGENTS.md#commit-rules). Report what remains unverified and stop the affected commit and stage exit if a required check cannot run.

## Optional headless OpenCode delegation

The lead can use Xiaomi MiMo-V2.6-Pro for scoped, read-only exploration when the provider and input scope are authorized. Follow the [headless OpenCode runbook](../.agents/skills/write-implementation-plan/references/opencode.md) for model setup, isolated scratch inputs, the brief attachment, and evidence review.
