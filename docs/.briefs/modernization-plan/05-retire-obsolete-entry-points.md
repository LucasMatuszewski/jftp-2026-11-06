# Retire the browser applet and inactive Mac adapter

Plan: docs/modernization-plan.md

Task: 05-retire-obsolete-entry-points

Verified baseline: `52c10e6c19c47efd972b24b87a6974d2c6927cf8`.

## Assignment

Own only `pom.xml`, `src/main/java/com/myjavaworld/jftp/JFTP.java`, `src/main/java/com/myjavaworld/jftp/JFTPApplet.java`, and `src/main/java/com/myjavaworld/jftp/OSXAdapterOld.java`. Remove the applet, the constructor overload and boolean used exclusively by it, the applet exit branch, the inactive adapter, and the provided AppleJavaExtensions dependency. Keep normal desktop constructor initialization order and exit cleanup. The desktop main class, package layout, reflective `OSXAdapter`, and `JFTPApplication` remain intact. Keep release 8 and JUnit 3 for this change.

Before editing, inspect all call sites using `rg -n 'JFTPApplet|OSXAdapterOld|extends JApplet|new JFTP\(|boolean applet' src/main/java` and the baseline JAR with `jar tf target/jftp-5.0.2-SNAPSHOT.jar`. Both retired classes should still be present in the initial JAR. Report unexpected callers or source changes and stop. After editing, run `git diff --check`, review the exact four paths, and hand the candidate to the lead. Do not modify tests or use a shared target while the lead verifies. The lead checks that both obsolete classes and the Apple dependency are absent, the desktop and reflective adapter classes are packaged, `mvn clean verify` passes the 11 baseline tests, and Windows startup and exit pass with isolated state.

Do not stage changes until the lead confirms verification and authorizes the exact paths. Use the commit subject `Desktop: retire the obsolete entry points`. Report the commit ID and exact contents.
