# Prepare and verify the Java 25 build

Plan: docs/modernization-plan.md

Task: 06-java-25-package

Starting application snapshot: `6326e8f` plus the four uncommitted paths listed in [task 05](05-retire-obsolete-entry-points.md). The lead reserves the production POM until task 05 passes desktop QA and is committed.

## Scope and order

1. Own only the new scratch directory `C:/Users/labuser/AppData/Local/Temp/jftp-modernization-373926d3-9bb2-4c0d-ac99-2325d14367a8/java25-probe`. Stage a copy of the tracked baseline with `git archive --format=zip --output=<scratch>/baseline.zip 6326e8f` and `Expand-Archive` into `<scratch>/tree`. Copy the task 05 candidate `pom.xml` and `JFTP.java` from the plan worktree into that copy using literal paths. Remove only the two retired Java files from the scratch tree. Record SHA-256 for the two current copied files. This freezes the Java inputs while Windows QA uses the task 05 package.
2. Use Temurin 25.0.4.1 and Maven 3.9.12 from `C:/Users/labuser/AppData/Local/Temp/jftp-modernization-tools/jftp-bootstrap/apache-maven-3.9.12/bin/mvn.cmd`. Copy the existing Maven cache from the sibling `repository` into the scratch directory. Give Maven a scratch `maven.repo.local` and `user.home`. Run `clean verify` on the frozen release 8 snapshot and show that the assertion for Java 25 bytecode fails against class header major version 52. The existing suite must pass 11 tests. Stop for unexpected test failures, a changed snapshot, or a permission denial.
3. Own only `<scratch>/tree/pom.xml` for this experiment. Set `maven.compiler.release` to 25. Add Maven Enforcer 3.6.2 with the `enforce` goal on `validate` and `requireJavaVersion` range `[25,26)`. Upgrade Maven JAR to 3.5.1, Assembly to 3.8.0, and Source to 3.4.0. Keep Compiler 3.16.0 and the current Surefire and JUnit for this step. Keep the manifest, launcher filtering, existing JARs and three distributions, and the legacy publication configuration. Determine the cause of new warnings and report unresolved risks.
4. Run `clean verify` with the isolated cache. Check the discovered 11 tests, bytecode major version 69, ZIP/TAR/directory archive layout, executable LF POSIX launcher, runtime libraries and resource/manifest contents. When a second installed JDK or JRE is available, show that Maven `validate` rejects it before compilation under the new POM. Report if the machine has no such runtime. Do not launch the app, change shared files, update global tools, or stage a commit.

## Report

Give the lead the baseline and candidate logs, the POM diff and bytes, verified artifact paths, test counts, tool versions, and any unverified requirement. The lead will integrate the approved POM after task 05 and repeat Maven and Windows desktop QA on the branch before delegating the commit.
