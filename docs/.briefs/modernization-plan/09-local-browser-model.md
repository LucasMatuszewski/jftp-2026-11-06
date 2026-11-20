# Modernize local browser presentation types in isolation

Plan: docs/modernization-plan.md

Task: 09-local-browser-model

Baseline: the isolated `java25-jupiter-integrated-probe` built from the task 05, 06 and 07 candidates. All 11 cases passed Maven verification. This task prepares a presentation-only candidate while native desktop access is pending.

## Assignment

1. Use scratch root `C:/Users/labuser/AppData/Local/Temp/jftp-modernization-373926d3-9bb2-4c0d-ac99-2325d14367a8/local-browser-probe`. Freeze the sibling `java25-jupiter-integrated-probe/tree` by copying its `pom.xml`, `src`, `README.md`, `AGENTS.md`, `LICENSE.txt` and `NOTICE.txt` to the new scratch `tree`. Copy its sibling Maven `repository` to the scratch root and use a fresh `home` for tests. Leave the plan worktree and the source probe untouched.
2. Own only the scratch `src/main/java/com/myjavaworld/jftp/LocalPane.java` and `LocalFileTableModel.java`. Use generics for the local directory chooser's model, renderer and backing `Vector`. Retain synchronized collection behavior, directory canonicalization and root-to-leaf order. Use autoboxing for object-valued status counts and byte sizes, retaining `Integer` for counts and `Long` for sizes. Use `Class<?>` for the column class return type. Use normal Java overrides and preserve existing CRLF. Leave `MComboBox`, table sorting, file filtering, file operations, session, and persistence code unchanged.
3. Characterize the display table's file, size and modified columns with a synthetic file in a temporary directory. Use the same standalone headless Java probe on the original and candidate JARs and report both results. Run Maven 3.9.12 `clean verify` with Temurin 25.0.4.1, Maven executable `C:/Users/labuser/AppData/Local/Temp/jftp-modernization-tools/jftp-bootstrap/apache-maven-3.9.12/bin/mvn.cmd`, separate `maven.repo.local` and `user.home`, and `-Djava.awt.headless=true`. Expect all 11 cases. Review the exact diff and unrelated changes in the scratch copy. Stop and report unexpected assertions, changed behavior, or permission denials.

## Report

Report scratch paths, hashes, scoped diffs, baseline and candidate probe outputs, Maven command and test counts. Do not open the desktop app or commit. The lead runs native Windows QA of listing, directory selection, keyboard focus and item counts, then integrates and verifies this candidate only after the preceding stages.
