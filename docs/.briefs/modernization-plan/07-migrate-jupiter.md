# Prepare and characterize the JUnit Jupiter migration

Plan: docs/modernization-plan.md

Task: 07-migrate-jupiter

Frozen app input: `6326e8f` plus the four retired-entry-point changes from [task 05](05-retire-obsolete-entry-points.md). Keep the plan worktree untouched while earlier gates run.

## Scope and steps

1. Own the new scratch directory `C:/Users/labuser/AppData/Local/Temp/jftp-modernization-373926d3-9bb2-4c0d-ac99-2325d14367a8/jupiter-probe`. Archive `6326e8f` to ZIP with `git archive`, extract to `<scratch>/tree`, and freeze the task 05 candidate there by copying its current `pom.xml` and `JFTP.java` and deleting only the scratch copies of `JFTPApplet.java` and `OSXAdapterOld.java`. Record the two copied files' SHA-256. Set compiler release 25 in the scratch POM. Keep the previous packaging plugins for this isolated test experiment. Use Maven 3.9.12 from `C:/Users/labuser/AppData/Local/Temp/jftp-modernization-tools/jftp-bootstrap/apache-maven-3.9.12/bin/mvn.cmd` and Temurin 25.0.4.1. Copy the shared cache from the sibling `repository` and use the copy as `maven.repo.local`; use a scratch Java home for the test process.
2. Edit only the scratch `pom.xml` and these five scratch tests: `MTableTest`, `SwingWorkerTest`, `ConnectionProfileTest`, `LocalFileFilterTest`, `ZipRoundTripTest` under their existing `src/test/java` paths. Switch JUnit 3.8.1 to test-scoped `org.junit.jupiter:junit-jupiter:6.1.3` and Surefire 3.5.5 to 3.6.1. Change only JUnit imports, inheritance, discovery and lifecycle annotations, and the argument order for assertion messages where required. Retain method names, inputs, cleanup, assertions, waits, and failure conditions.
3. Run Maven `clean verify` with the scratch cache, `-Djava.awt.headless=true` and isolated `-Duser.home`. Confirm discovery by the JUnit Platform provider and results 3/1/3/3/1 across the five classes. Compare the source diff with the originals and explain each changed assertion call. Stop if a changed assertion or unexpected failure would be required.
4. Reproduce the popup regression described in `src/test/README.md` in the scratch tree only. Save the fixed scratch `MTable.java`, restore the parent of `14e62ce` version there, and select its old Mac branch explicitly for the Windows headless run. Run Maven `-Dtest=MTableTest test` and inspect Surefire reports. Expected: selected-row test passes, the unselected-row and below-rows tests fail with the original selection. Restore the fixed scratch source and rerun `clean verify`. Report any deviation, exact commands and expected versus actual failures. Do not change production files or stage a commit.

## Report

Provide scratch paths, exact POM changes, five test diffs, provider and counts, and both popup results. The lead integrates only migration mechanics after the Java 25 package commit and runs the integrated suite and packaged check.
