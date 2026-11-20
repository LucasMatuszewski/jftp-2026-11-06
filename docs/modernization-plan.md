# Restore jFTP startup, then modernize to Java 25 LTS

## Preflight

Before stage 1, and again before any work item affected by a documentation change:

- [ ] Read the [repository instructions](../AGENTS.md), applicable nested instructions, the [as-is PRD](PRD.md), the [current-state ADR](ADR/000-main-architecture.md) and any other relevant ADRs, the [architecture documentation](architecture.md), and this plan. The repository uses `PRD.md` for the product baseline and has no separate PLD.
- [ ] Compare requirements and acceptance criteria with the plan and the relevant architecture decisions. Check the desktop entry point, packaging, dependencies, Swing worker boundaries, FTP/FTPS behavior, persisted data, and test, Manual QA, and commit gates wherever the work touches them.
- [ ] Check the revision and status of each source. The PRD, current-state ADR, and diagrams cite revisions with the same committed `src/main` and `pom.xml` as `513b9a5`. The PRD scopes the as-is desktop behavior, and this plan scopes the migration. The ADR's test-placeholder note is historical. Use the [test runner](../src/test/README.md) and current test sources to verify the 11-case baseline.
- [ ] Check the working tree and record the starting revision and existing local changes. Reconcile any uncommitted startup work at handoff against the stage 1 gate and record what still needs verification. Keep unrelated edits and artifacts out of assignments and commits. Confirm differences against the current source, configuration, and tests, and use the agreed transition in this plan as the target for its stage.
- [ ] Apply repository instructions to workflow, agreed product requirements to behavior, and accepted ADRs to technical decisions. Use this plan for agreed changes, ordering, and gates. Record any discrepancy with the document references and evidence. Correct verified documentation drift in the corresponding logical change. Stop affected work and ask the user to resolve any conflict that could change behavior, acceptance criteria, an architectural decision, or a verification gate.

## Execution and delegation

- The lead agent delegates research, implementation, test execution, and QA. It defines scope, acceptance criteria, dependencies, and file ownership, then reviews diffs and evidence, coordinates integration, and verifies gates. For each verified logical change, the lead authorizes one responsible worker to stage the agreed paths and create the local commit, then checks its exact contents.
- Use **Luna** for research, read-only analysis, and QA by default. Use **Sol** for code and for analysis or QA that requires deeper domain knowledge. Use **Astra** only for an exceptionally difficult problem and record why it is needed.
- Run independent assignments in parallel in one checkout. Give each writer explicit, disjoint paths, including test files and generated artifacts. Assign shared files to one writer at a time. If concurrent writes to overlapping paths are unavoidable, document why a worktree is needed, assign its owner, and have the lead agent integrate and verify the result.
- Overlap QA with implementation only when the tested commit or frozen snapshot is identified and the outputs are isolated. Give each concurrent run its own build directory, extracted package, synthetic preferences, test-server state, logs, and screenshots as applicable. Run the relevant gate again on the integrated candidate before committing.
- Keep the user informed at stage gates with the outcome, verification evidence, and commit IDs. Ask for a decision when scope, behavior, or architecture would change, and surface gates that remain blocked after investigation with the evidence and the decision or prerequisite needed to resume.

## 1. Establish a working baseline

- Re-run the 11 existing tests using the [verified runner](../src/test/README.md). Use their current assertions as the behavioral baseline.
- Make the smallest build changes needed for `mvn clean verify`: switch the jMethods repository to its verified HTTPS endpoint, configure the compiler for `--release 8`, add AppleJavaExtensions 1.4 as a compile-only dependency, and pin Surefire 3.5.5 for JUnit 3. Keep the existing application dependencies and source behavior through this stage. [Compiler plugin](https://maven.apache.org/plugins/maven-compiler-plugin/plugin-info.html) · [Surefire 3.5.5 JUnit support](https://maven.apache.org/surefire-archives/surefire-3.5.5/maven-surefire-plugin/examples/junit.html)
- Fix both launch scripts to resolve the application JAR beside the script, consistent with the assembly layout. Keep the runtime dependencies under `lib/`. Verify the Windows launcher from a different working directory using an extracted distribution path that contains spaces.
- Launch the packaged application on Windows with an isolated Java home and synthetic preferences. Confirm the initial disconnected tab, local browsing, bundled help, and clean exit. Run all 11 tests again. Make a production-code change at this stage only if a reproduced startup failure requires it.

**Gate.** The package builds, the real window opens, all 11 behavioral tests pass, and the applicable verification checklist below is complete. Commit the build restoration, launcher fix, and any necessary application fix as separate verified changes. Then continue into modernization.

## 2. Modernize in independently verified commits

1. **Retire obsolete entry points.** Remove the browser applet and its applet-specific constructor and exit branch. Remove the unreferenced `OSXAdapterOld` and the compile-only Apple dependency. Preserve the packaged desktop entry point and the active macOS integration.
2. **Move to Java 25 LTS.** Require JDK 25 for the build and set the compiler release to 25. Upgrade the build plugins needed for verification and packaging, including Maven JAR 3.5.1 and Assembly 3.8.0. Preserve the archive layout, resource inclusion, and launcher contract established in stage 1. [Java support roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) · [Apache Maven plugins](https://maven.apache.org/plugins/)
3. **Upgrade the test runner.** Port the 11 existing cases to JUnit Jupiter 6.1.3 with Surefire 3.6.1 in a dedicated test and tooling commit. Verify that all 11 cases are discovered and that their inputs, assertions, and failure conditions still match the baseline. Repeat the historical popup regression check to show that the migrated tests still detect it. [JUnit 6.1.3](https://docs.junit.org/6.1.3/_exports/junit-user-guide-6.1.3.html) · [Surefire JUnit support](https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit.html)
4. **Refactor by behavior area.** Modernize collection types and boxing in profiles, local-file handling, and session code. Use try-with-resources in ZIP and persistence paths while preserving close order, progress callbacks, and error behavior. Keep the Swing worker contract, FTP client/parser mappings, serialized field names and types, and `serialVersionUID` values. Retain `ftpapi` 3.0.0 and JavaHelp 2.0.05. [FTP library publisher](https://www.jmethods.com/ftpapi/download.html)

**Gate.** The packaged app launches on Java 25 on Windows, all 11 migrated tests pass with their baseline assertions, and the loopback FTP/FTPS and preference and favorite reload checks pass. Complete the verification checklist and record the verified local commits.

## 3. Protect behavior and verify each change

- Treat a newly failing test as a regression in the application or build first. Reproduce it on the last green commit, compare observable behavior, fix the changed code, and rerun the affected test and full suite. Changes to tests during the JUnit migration are limited to framework mechanics. Any change to expected behavior requires separate agreement.
- Before refactoring transfer or persistence code, record a Windows baseline against a real loopback FTP/FTPS server with synthetic files and credentials. Recheck login success and refusal, filtered listings, binary upload and download byte equality, ZIP round trips, abort before the next batch item, and the behavior of a second session tab. Save synthetic preferences and favorites on the baseline and verify that Java 25 reloads them.
- For each runtime-impacting commit, run `mvn clean verify` and repeat the affected native desktop flow. For packaging commits, launch the extracted distribution outside the IDE. Record the commands, results, and relevant screenshots in `docs/qa/screens`. Windows is the completion gate. Report native macOS and Linux coverage separately.
- Update the build and test instructions with the verified commands, refresh affected source-derived docs, and keep each verified logical change in its own local commit.

### Verification checklist for both stage exits

- [ ] Review the integrated diff and test evidence. Confirm each logical change passed its required checks before the lead authorized its assigned worker to commit it, then verify the resulting commit contents.
- [ ] Complete all applicable [Manual QA requirements](../AGENTS.md#manual-qa) on the real Windows desktop with isolated synthetic state and real services where applicable. Verify startup, success, cancellation, error paths, actual outputs and persistence, responsiveness, focus, keyboard navigation, UI text and visuals, and extracted-package startup for packaging changes.
- [ ] Inspect runtime and supporting-service logs. Record steps, environment, outcomes, and screenshots of changed screens and relevant states. If required native QA or another verification step cannot run, stop the affected commit and stage exit and tell the user exactly what remains unverified.

## Optional headless OpenCode delegation

Use Xiaomi MiMo-V2.6-Pro for a scoped read-only second opinion when the user has authorized provider usage and any data sent to it. Verify `opencode --version` and `opencode run --help` against the installed CLI. OpenCode 2.2.5 supports the command below. Start with a synthetic prompt containing no repository data once provider access and usage are authorized.

Point `OPENCODE_CONFIG` at a dedicated `opencode.json` outside the checkout. Provide `MIMO_API_KEY` through the process environment. The example uses the pay-as-you-go endpoint. For a token plan, use the exact endpoint for the authorized account. [OpenCode configuration](https://opencode.ai/docs/config/) · [Xiaomi integration](https://mimo.mi.com/docs/en-US/tokenplan/integration/opencode)

```json
{
  "$schema": "https://opencode.ai/config.json",
  "provider": {
    "mimo": {
      "npm": "@ai-sdk/openai-compatible",
      "options": {
        "baseURL": "https://api.xiaomimimo.com/v1",
        "apiKey": "{env:MIMO_API_KEY}"
      },
      "models": {
        "mimo-v2.6-pro": { "name": "mimo-v2.6-pro" }
      }
    }
  },
  "permission": {
    "*": "deny",
    "read": "allow",
    "glob": "allow",
    "grep": "allow",
    "list": "allow",
    "bash": "deny",
    "edit": "deny",
    "task": "deny",
    "external_directory": "deny"
  }
}
```

Set `$openCodeScratch` and `$openCodeConfig` to the absolute paths of the dedicated scratch directory and configuration file. Use both outside the checkout and keep only explicitly authorized copies in scratch. Use a sanitized process environment and keep secrets out of prompts and attached files. The lead agent reviews the returned evidence before using any conclusion. [OpenCode CLI](https://opencode.ai/docs/cli/) · [OpenCode permissions](https://opencode.ai/docs/permissions/)

```powershell
if ([string]::IsNullOrWhiteSpace($env:MIMO_API_KEY)) {
    throw 'MIMO_API_KEY must be set in the process environment.'
}

$previousOpenCodeConfig = $env:OPENCODE_CONFIG
Push-Location -LiteralPath $openCodeScratch -ErrorAction Stop
try {
    $env:OPENCODE_CONFIG = $openCodeConfig
    opencode run --standalone --model mimo/mimo-v2.6-pro --agent plan --format json 'Answer the scoped read-only question.'
    if ($LASTEXITCODE -ne 0) {
        throw "OpenCode exited with code $LASTEXITCODE."
    }
} finally {
    $env:OPENCODE_CONFIG = $previousOpenCodeConfig
    Pop-Location
}
```
