# Repository guidance

jFTP is a desktop FTP client. Use Polish in chat unless the user requests another language. Write application documentation, code and code comments in English.

## Navigating the codebase

- Start with [RepoMap](docs/jftp-repo-map.rust.md) to find entry points, understand the project structure and locate Java declarations. The map is selective.
- Use the [code graph](docs/CodeGraph/README.md) to explore cross-file references, callers and change impact. Confirm important relationships in the original source.
- Use the [full Java Repomix](docs/Repomix/jftp-repomix.xml) to follow implementation across files. The [compressed pack](docs/Repomix/jftp-repomix.compressed.xml) is useful for orientation and omits comments and parts of the implementation.
- For localization, help, packaging and build configuration, start with the [resource map](docs/jftp-zasoby.md) and read the relevant parts of the [resource pack](docs/Repomix/jftp-repomix.resources.xml).

Read only the sections needed for the task. Verify original definitions, callers and configuration before making changes. Refresh affected artifacts when their source changes.
