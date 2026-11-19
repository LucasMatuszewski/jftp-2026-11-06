# Repository guidance

## Project

jFTP is a desktop FTP client. Use Polish in chat unless the user requests another language. Write application documentation, code and code comments in English.

Start with the [documentation index](docs/README.md).

## Navigating the codebase

- Start with [RepoMap](docs/jftp-repo-map.rust.md) to find entry points, understand the project structure and locate Java declarations. The map is selective.
- Use the [code graph](docs/CodeGraph/README.md) to explore cross-file references, callers and change impact. Confirm important relationships in the original source.
- Use the [full Java Repomix](docs/Repomix/jftp-repomix.xml) to follow implementation across files. The [compressed pack](docs/Repomix/jftp-repomix.compressed.xml) is useful for orientation and omits comments and parts of the implementation.
- For localization, help, packaging and build configuration, start with the [resource map](docs/jftp-zasoby.md) and read the relevant parts of the [resource pack](docs/Repomix/jftp-repomix.resources.xml).

Read only the sections needed for the task. Verify original definitions, callers and configuration before making changes. Refresh affected artifacts when their source changes.

## Agent Workflow

Agreed requirements and existing PRD/ADR/design decisions govern changes; define expected behavior before code.

### TDD Rules

For every feature and bug fix:
1. Start from the specification, not the existing implementation.
2. Write or extend tests **before** production code.
3. Run the new tests and confirm they fail for the expected reason.
4. Implement the minimum code needed to make them pass.
5. Run the full verification suite for the changed scope.
6. Refactor only while tests stay green.
7. **Manually validate the running application** (see Manual QA below). Automated tests — especially E2E — can produce false passes; a task is not done until the real app has been exercised by hand.

If the area has no suitable test infrastructure yet, add it as part of the task — do not silently skip tests.

### Manual QA

Required for every task affecting the running application:

1. Start the real app with its verified command and authorized local/test configuration, using isolated synthetic state without changing personal preferences or data. Confirm startup separately from compilation/build and tests.
2. Exercise the complete affected flow with synthetic inputs, including success, cancellation and relevant error paths. Verify actual outputs, state changes and persisted results, not success messages alone.
3. Use native desktop tooling or human verification to exercise real windows, dialogs and controls. Check responsiveness, focus and keyboard navigation. Headless tests do not prove GUI behavior.
4. For packaging changes, also launch the packaged build outside the IDE; verify runtime resources and dependencies.
5. Capture each changed screen and relevant state in `docs/qa/screens`. Inspect application and supporting-service runtime logs for errors. Include selected screenshots in a commit when they help review the change and contain only synthetic test data.
6. Check UI text against agreed language/localization requirements and visual changes against approved references or existing UI conventions.
7. Report steps, environment and evidence; fix failures before committing. If desktop access, a service or another prerequisite is unavailable, state exactly what remains unverified. The runtime task is incomplete until required Manual QA passes.

### Test Strategy

| Type | Mocks |
|---|---|
| Unit | Dependencies may be isolated |
| Integration | Mocks allowed when useful; endpoint fixtures require a fresh real response |
| E2E and Manual QA | **Zero mocks; fully working real stack, including real LLM calls where applicable** |

#### Endpoint and database mocks

Before creating or refreshing any unit/integration endpoint or database mock, make a fresh real query in the authorized local/test environment. Save the complete response verbatim to a file, then derive the mock from that capture, never documentation or invented objects. Record provenance separately without secrets. Existing fixtures do not replace a fresh capture.

Documentation can be outdated: invented request/response shapes can pass mocked tests while failing against the real endpoint. Use synthetic authorized data; never commit private responses or credentials. Missing access or authorized budget blocks mock creation/refresh and required real checks.

### Verification

Verify the changed scope before committing: startup, tests and Manual QA for runtime changes; content/links for documentation. Tests passing ≠ app working.

### Commit Rules

- Commit only after verification passes and the changed scope is in a working state.
- Keep commits focused: one logical change per commit.
- Create a local commit after each verified logical change, including documentation-only changes.
- Format: `Area: short summary`.
- Do **not** push to remote unless the user explicitly asks.
- Stage explicit paths and preserve unrelated work.

### Completion Criteria

Complete only with agreed behavior, honest verification, required QA evidence and a reviewable result. Investigate failures/new warnings; report blockers.

Update affected repository instructions and documentation in the same logical change whenever enduring project facts or policies change.
