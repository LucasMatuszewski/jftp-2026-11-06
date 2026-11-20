# Check the modernization baseline

Plan: docs/modernization-plan.md
Task: 00-preflight

## Assignment

Collect read-only evidence for the lead before stage 1. Compare the current checkout and any existing startup edits with the checks below. The lead decides how to resolve differences. Use the files named below as evidence.

## Steps

1. From the repository root, run `git rev-parse HEAD` and `git status --short`. Record both. Run `git diff --name-status 513b9a5590937b1fc7ded6c6b4fab93b8872afc4 HEAD -- src/main pom.xml src/test/java` to find committed changes since the reviewed baseline. Run `git status --short --untracked-files=all -- src/main/java src/test/java` to list staged, unstaged, and untracked Java paths individually. Use both lists in steps 4 and 5. Run `git diff HEAD -- pom.xml src/main/assembly/binary.xml src/main/scripts/jftp.bat src/main/scripts/jftp.sh` to examine uncommitted build and launcher changes. If either revision cannot be read or the status check fails, report the error and stop the comparison.
2. Inspect `pom.xml`. Stage 1 expects the jMethods repository URL `https://www.jmethods.com/mvn-repo`, compiler release 8, compile-only AppleJavaExtensions 1.4, and Surefire 3.5.5 with JUnit 3.8.1. Runtime dependencies stay at ftpapi 3.0.0 and JavaHelp 2.0.05. Check each item by name and report whether existing edits cover it. Read the manifest and resource declarations. The entry point is `com.myjavaworld.jftp.JFTPApplication`. The distribution must bundle UI text, images, and JavaHelp.
3. Compare `src/main/assembly/binary.xml` with `src/main/scripts/jftp.bat` and `src/main/scripts/jftp.sh`. The project JAR and launchers belong at the package root and dependencies under `lib/`. Both launchers need to resolve the project JAR beside the script. Report whether the assembly and scripts agree, including whether either script depends on the caller's current directory. Use source evidence for this step.
4. For each application Java path in either list from step 1, check the relevant boundary and cite the symbol or line. Include new paths and report removed paths with the boundary they affected. In `src/main/java/com/myjavaworld/jftp/JFTPApplication.java`, startup shows the main window and creates an initial disconnected session. In `src/main/java/com/myjavaworld/gui/SwingWorker.java`, background work posts completion to the Swing EDT. In `src/main/java/com/myjavaworld/jftp/FTPSession.java`, profile-selected FTP client and parser mappings, SSL setup, and per-tab state should remain intact. In `src/main/java/com/myjavaworld/jftp/JFTP.java` and `src/main/java/com/myjavaworld/jftp/FavoritesManager.java`, keep reading and writing the existing settings and favorites formats. Report any change to these boundaries. Report no changed application Java paths only if both lists contain none.
5. Run `git ls-files -- "src/test/java/*Test.java"`. The expected classes are `MTableTest` (3), `SwingWorkerTest` (1), `ConnectionProfileTest` (3), `LocalFileFilterTest` (3), and `ZipRoundTripTest` (1). Count methods beginning with `public void test` in those files. Report counts and any test source added, edited, or removed in either list from step 1. This verifies the inventory. Execution evidence comes from task `01-verify-baseline`.

## Report

Send the lead the revision and relevant status paths, then a table with columns **Check**, **Pass / Mismatch / Unverified**, and **Evidence**. Include the remaining stage 1 checks: a clean Maven verification, the 11 test results, and packaged Windows startup with synthetic state. Report missing files, changed assumptions, or conflicting evidence with the affected step. Keep the checkout and user data untouched. For this assignment run only the read-only checks above, with no build, desktop launch, network calls, or commits.
