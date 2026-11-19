"""Stage and verify jFTP text resources for a UTF-8 Repomix XML pack."""

import argparse
from pathlib import Path, PurePosixPath
import subprocess
from xml.etree import ElementTree


def selected_sources():
    root_files = {"pom.xml", "README.md", "LICENSE.txt", "NOTICE.txt", ".classpath", ".project"}
    resource_roots = ("src/main/resources/", "src/main/resources_de/", "src/main/resources_zh_TW/")
    help_extensions = {".html", ".xml", ".dtd", ".txt", ".css"}
    tracked = subprocess.check_output(["git", "ls-files", "-z"]).decode("utf-8").split("\0")
    return sorted(path for path in tracked if (
        path in root_files
        or path.startswith(".settings/")
        or (path.startswith(resource_roots) and path.endswith(".properties"))
        or (path.startswith("src/main/help/") and PurePosixPath(path).suffix in help_extensions)
        or path.startswith(("src/main/assembly/", "src/main/scripts/"))
    ))


def source_text(path):
    raw = Path(path).read_bytes()
    try:
        return raw.decode("utf-8"), False
    except UnicodeDecodeError:
        if not path.startswith("src/main/resources_de/") or not path.endswith(".properties"):
            raise
        return raw.decode("iso-8859-1"), True


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument("--stage", type=Path, help="empty or new directory outside the repository")
    mode.add_argument("--verify", type=Path, help="Repomix XML file to compare with the sources")
    args = parser.parse_args()

    sources = selected_sources()
    pieces = {path: source_text(path) for path in sources}
    legacy_count = sum(legacy for _, legacy in pieces.values())
    if args.stage is not None:
        staging = args.stage.resolve()
        if staging.is_relative_to(Path.cwd()) or (staging.exists() and any(staging.iterdir())):
            parser.error("choose an empty or new staging directory outside the repository")
        for path, (content, _) in pieces.items():
            destination = staging / path
            destination.parent.mkdir(parents=True, exist_ok=True)
            destination.write_bytes(content.encode("utf-8"))
        print(f"Staged {len(pieces)} files, including {legacy_count} converted from ISO-8859-1")
        return

    entries = ElementTree.parse(args.verify).findall("./files/file")
    packed_paths = {entry.attrib["path"] for entry in entries}
    if len(entries) != len(packed_paths) or packed_paths != pieces.keys():
        raise SystemExit(f"Path mismatch. Missing: {sorted(pieces.keys() - packed_paths)}; "
                         f"extra: {sorted(packed_paths - pieces.keys())}")
    trimmed_count = 0
    for entry in entries:
        path = entry.attrib["path"]
        source = pieces[path][0].replace("\r\n", "\n").replace("\r", "\n")
        packed = entry.text or ""
        if packed == source:
            continue
        if packed != source.strip():
            raise SystemExit(f"Packed content differs: {path}")
        trimmed_count += 1
    print(f"Verified {len(entries)} files, including {legacy_count} decoded from ISO-8859-1 "
          f"and {trimmed_count} trimmed at file boundaries")


if __name__ == "__main__":
    main()
