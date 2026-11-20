# Prepare profile and local-file collection modernization

Plan: docs/modernization-plan.md

Task: 10-profile-local-models

Frozen starting revision: `0f4122958e3528713b940cb2931d9f3c7b21f591`.

1. In the existing jFTP temp root `C:/Users/labuser/AppData/Local/Temp/jftp-modernization-373926d3-9bb2-4c0d-ac99-2325d14367a8`, create a new `profile-local-probe` scratch directory. Extract a `git archive` of the pinned revision to `<scratch>/tree`. Copy the Maven cache from the sibling `local-browser-probe/repository` and give the new probe its own `user.home` for tests.
2. Own only `RemoteHost.java`, `LocalFile.java` and `LocalFileComparator.java` under the scratch `src/main/java/com/myjavaworld/jftp`. Type the command and local-file lists and their array conversions. Use ordinary boxing and unboxing for the existing `Integer` implicit SSL port while preserving the null fallback. Use `Comparator<LocalFile>` for the local sort, keeping the current logic and method names. Preserve serialized field types and IDs, the public overloads and return types, whitespace and order in commands, the null and filter behavior of local directory listing, and the current sort outcomes. Retain the inherited binary bridge for `compare(Object,Object)`. Leave session, transfer, ZIP and on-disk persistence code untouched.
3. Run Maven 3.9.12 `-B -o clean verify` with Temurin 25.0.4.1 from the scratch tree. Maven is under `C:/Users/labuser/AppData/Local/Temp/jftp-modernization-tools/jftp-bootstrap/apache-maven-3.9.12/bin/mvn.cmd`. Pass the scratch repository and home with `-Dmaven.repo.local` and `-Duser.home`, and pass `-Djava.awt.headless=true`. Verify all 11 cases and compare `VerifyPersistence.java schema` output for the candidate with sibling `persistence-baseline/serialized-schema.txt`. Use the normal escalated command path if Java ZIPFS encounters the known sandbox error. Stop after an automatic approval denial.
4. Review exact diffs and report scratch paths, original and updated hashes, results, and any surprising behavior. Do not open the desktop app, edit production files, or commit. The lead owns integrated verification and desktop QA.
