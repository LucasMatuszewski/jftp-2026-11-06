# Verify the existing characterization suite

Plan: docs/modernization-plan.md
Task: 01-verify-baseline

After the lead clears preflight, use the agreed baseline revision or snapshot and the [Windows test runner](../../../src/test/README.md). Confirm that the tracked Java and test inputs match that snapshot. Confirm the documented JDK and local test dependencies before running the suite. Use the runner's temporary output and synthetic user home. Keep tracked files, existing build output, and user settings untouched.

Run all five test classes and account for the 11 existing cases. Report the revision, JDK version, command, per-class counts, and any compile or test failures. Stop and identify the missing prerequisite if the snapshot, JDK, or local dependencies are unavailable. Send the evidence to the lead before baseline changes proceed. Packaged startup and desktop QA are separate checks in the [stage 1 gate](../../modernization-plan.md#1-establish-a-working-baseline).
