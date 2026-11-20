#!/bin/sh
script_dir=$(CDPATH='' cd -P "$(dirname "$0")" && pwd) || exit 1
exec java -jar "$script_dir/${project.build.finalName}.jar"
