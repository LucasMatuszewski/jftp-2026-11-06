# Restore the Maven baseline

Plan: docs/modernization-plan.md

Task: 02-restore-build

Starting application revision: `ef19cb82c5d10ef82e18d5530803f6a452263045`.

## Preflight evidence

The starting worktree was clean. Application and test sources and the POM match `513b9a5590937b1fc7ded6c6b4fab93b8872afc4`. The historical ADR predates the five characterization test classes. The test inventory is 11 cases, and the assembly declares its application JAR at the package root. The primary checkout has an uncommitted POM patch that implements the planned build changes. Leave the primary checkout untouched.

## Assignment

Own only `pom.xml` in the plan branch worktree. In the primary checkout, read the existing patch with `git -c safe.directory=D:/jftp -C D:/jftp diff -- pom.xml`. Reproduce its verified scope in the plan worktree. Use the HTTPS jMethods URL, compiler plugin 3.16.0 with release 8, provided AppleJavaExtensions 1.4, and Surefire 3.5.5. Preserve JUnit 3.8.1, both runtime dependencies, the desktop manifest, assembly, and the test sources. Review `git diff --check` and `git diff -- pom.xml`, then report the exact paths and diff for review.

The unmodified POM failed offline Maven verification during compiler plugin resolution. The lead runs the integrated Maven and desktop checks in isolation because the Windows sandbox denied access to dependency archives in direct javac testing. Do not commit until the lead confirms verification and authorizes staging `pom.xml`. Stop and report any discrepancy, unexpected diff, or permission failure. After approval use the subject `Build: restore the Maven baseline` and report the resulting commit ID and paths.
