# Resolve the packaged JAR from either launcher

Plan: docs/modernization-plan.md

Task: 03-fix-launchers

Starting application revision: `ef19cb82c5d10ef82e18d5530803f6a452263045` plus the Maven baseline patch.

## Assignment

Own `src/main/scripts/jftp.bat`, `src/main/scripts/jftp.sh`, and `src/main/assembly/binary.xml`. Preserve the manifest, project JAR at the package root, runtime dependencies in `lib/`, all three assembly formats, and resource and document inclusion. Both launchers must find the versioned JAR beside the script and work from a different working directory when either path has spaces. Preserve the caller's directory and Java exit status. Use normal Windows batch and POSIX shell conventions.

The prior Windows and shell launchers both returned 1 with “Unable to access jarfile” from the isolated caller's directory. The source scripts can have CRLF in this worktree. Give the POSIX script a shebang and use the assembly descriptor to package it with LF and executable permissions. Preserve the Windows launcher in the package root and filter the Maven artifact name in both scripts.

Run `git diff --check` and review the three paths. Report exact changes and any discrepancy. The lead runs an integrated `mvn clean verify` and validates the actual package from an unrelated Windows directory. Check the shell launcher separately with Git Bash and report native OS coverage separately. Do not commit until the lead confirms verification and authorizes staging the exact paths. After approval use the subject `Packaging: locate the JAR beside the launchers` and report the resulting commit ID and paths.
