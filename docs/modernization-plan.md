# Restore jFTP startup, then modernize to Java 25 LTS

## 1. Establish a working baseline

- Re-run the 11 existing tests using the [verified runner](../src/test/README.md). Use their current assertions as the behavioral baseline.
- Make the smallest build changes needed for `mvn clean verify`: switch the jMethods repository to its verified HTTPS endpoint, configure the compiler for `--release 8`, add AppleJavaExtensions 1.4 as a compile-only dependency, and pin Surefire 3.5.5 for JUnit 3. Keep the existing application dependencies and source behavior through this stage. [Compiler plugin](https://maven.apache.org/plugins/maven-compiler-plugin/plugin-info.html) · [Surefire 3.5.5 JUnit support](https://maven.apache.org/surefire-archives/surefire-3.5.5/maven-surefire-plugin/examples/junit.html)
- Fix both launch scripts to resolve the application JAR beside the script, consistent with the assembly layout. Keep the runtime dependencies under `lib/`. Verify the Windows launcher from a different working directory using an extracted distribution path that contains spaces.
- Launch the packaged application on Windows with an isolated Java home and synthetic preferences. Confirm the initial disconnected tab, local browsing, bundled help, and clean exit. Run all 11 tests again. Make a production-code change at this stage only if a reproduced startup failure requires it.

**Gate.** The package builds, the real window opens, and all 11 behavioral tests pass. Commit the build restoration, launcher fix, and any necessary application fix as separate verified changes. Then continue into modernization.

## 2. Modernize in independently verified commits

1. **Retire obsolete entry points.** Remove the browser applet and its applet-specific constructor and exit branch. Remove the unreferenced `OSXAdapterOld` and the compile-only Apple dependency. Preserve the packaged desktop entry point and the active macOS integration.
2. **Move to Java 25 LTS.** Require JDK 25 for the build and set the compiler release to 25. Upgrade the build plugins needed for verification and packaging, including Maven JAR 3.5.1 and Assembly 3.8.0. Preserve the archive layout, resource inclusion, and launcher contract established in stage 1. [Java support roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) · [Apache Maven plugins](https://maven.apache.org/plugins/)
3. **Upgrade the test runner.** Port the 11 existing cases to JUnit Jupiter 6.1.3 with Surefire 3.6.1 in a dedicated test-only commit. Verify that all 11 cases are discovered and that their inputs, assertions, and failure conditions still match the baseline. Repeat the historical popup regression check to show that the migrated tests still detect it. [JUnit 6.1.3](https://docs.junit.org/6.1.3/_exports/junit-user-guide-6.1.3.html) · [Surefire JUnit support](https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit.html)
4. **Refactor by behavior area.** Modernize collection types and boxing in profiles, local-file handling, and session code. Use try-with-resources in ZIP and persistence paths while preserving close order, progress callbacks, and error behavior. Keep the Swing worker contract, FTP client/parser mappings, serialized field names and types, and `serialVersionUID` values. Retain `ftpapi` 3.0.0 and JavaHelp 2.0.05. [FTP library publisher](https://www.jmethods.com/ftpapi/download.html)

## 3. Protect behavior and verify each change

- Treat a newly failing test as a regression in the application or build first. Reproduce it on the last green commit, compare observable behavior, fix the changed code, and rerun the affected test and full suite. Changes to tests during the JUnit migration are limited to framework mechanics. Any change to expected behavior requires separate agreement.
- Before refactoring transfer or persistence code, record a Windows baseline against a real loopback FTP/FTPS server with synthetic files and credentials. Recheck login success and refusal, filtered listings, binary upload and download byte equality, ZIP round trips, abort before the next batch item, and the behavior of a second session tab. Save synthetic preferences and favorites on the baseline and verify that Java 25 reloads them.
- For each runtime-impacting commit, run `mvn clean verify` and repeat the affected native desktop flow. For packaging commits, launch the extracted distribution outside the IDE. Record the commands, results, and relevant screenshots in `docs/qa/screens`. Windows is the completion gate. Report native macOS and Linux coverage separately.
- Update the build and test instructions with the verified commands, refresh affected source-derived docs, and keep each verified logical change in its own local commit.
