# Verify the existing characterization suite

Plan: docs/modernization-plan.md
Task: 01-verify-baseline

## Assignment

Run after the lead clears preflight. Use Windows PowerShell from the repository root. Test the Java and test sources from `513b9a5590937b1fc7ded6c6b4fab93b8872afc4`. The script checks that the current tracked sources match. Report any mismatch to the lead and wait for a revised brief if a different baseline is needed.

## Prerequisites

- Use a local JDK with `javac --release 8` support. Use `java.exe` from that same JDK.
- Set `JFTP_TEST_DEPS` for this PowerShell process to the local directory supplied by the lead. It must contain `ftpapi-3.0.0.jar`, `javahelp-2.0.05.jar`, `junit-3.8.1.jar`, and `AppleJavaExtensions-1.4.jar`. Report the exact missing prerequisite if the directory or any JAR is unavailable.
- The only writes are to the new temporary directory created by the script. Use the supplied local JARs. Keep source files, Maven output, user settings, and the Git index untouched.

## Run

Run this entire block in order. Stop and report the first error with its output. If the script reaches all five expected counts, report 11 passing cases.

```powershell
$ErrorActionPreference = 'Stop'
$baseline = '513b9a5590937b1fc7ded6c6b4fab93b8872afc4'
$prefix = git rev-parse --show-prefix
if ($LASTEXITCODE -ne 0 -or $prefix) { throw 'Run from the repository root.' }
git diff --quiet $baseline -- src/main/java src/test/java
if ($LASTEXITCODE -ne 0) { throw 'Java sources differ from the baseline or Git could not read it.' }
git rev-parse HEAD
if ($LASTEXITCODE -ne 0) { throw 'Could not read the current revision.' }

$javac = (Get-Command javac -CommandType Application).Source
$java = Join-Path (Split-Path -Parent $javac) 'java.exe'
if (-not (Test-Path -LiteralPath $java -PathType Leaf)) { throw 'java.exe is missing beside javac.' }
& $javac -version
if ($LASTEXITCODE -ne 0) { throw 'Could not read the compiler version.' }
& $java -version
if ($LASTEXITCODE -ne 0) { throw 'Could not read the Java runtime version.' }

if ([string]::IsNullOrWhiteSpace($env:JFTP_TEST_DEPS)) { throw 'JFTP_TEST_DEPS is unset.' }
if (-not (Test-Path -LiteralPath $env:JFTP_TEST_DEPS -PathType Container)) { throw 'JFTP_TEST_DEPS is not a directory.' }
$dependencyDir = (Resolve-Path -LiteralPath $env:JFTP_TEST_DEPS).Path
$jars = 'ftpapi-3.0.0.jar', 'javahelp-2.0.05.jar', 'junit-3.8.1.jar', 'AppleJavaExtensions-1.4.jar'
$dependencies = foreach ($jar in $jars) {
    $jarPath = Join-Path $dependencyDir $jar
    if (-not (Test-Path -LiteralPath $jarPath -PathType Leaf)) { throw "Missing dependency: $jar" }
    $jarPath
}
$dependencyClasspath = $dependencies -join ';'

$testRoot = Join-Path ([IO.Path]::GetTempPath()) ('jftp-tests-' + [guid]::NewGuid())
$classes = Join-Path $testRoot 'classes'
$testClasses = Join-Path $testRoot 'test-classes'
$testHome = Join-Path $testRoot 'home'
New-Item -ItemType Directory -Path $classes,$testClasses,$testHome | Out-Null
$cases = [ordered]@{
    'com.myjavaworld.gui.MTableTest' = 3
    'com.myjavaworld.gui.SwingWorkerTest' = 1
    'com.myjavaworld.jftp.ConnectionProfileTest' = 3
    'com.myjavaworld.jftp.LocalFileFilterTest' = 3
    'com.myjavaworld.zip.ZipRoundTripTest' = 1
}
$sources = @(git ls-files -- 'src/main/java/*.java')
if ($LASTEXITCODE -ne 0 -or $sources.Count -eq 0) { throw 'Could not list tracked Java sources.' }
$testFiles = @($cases.Keys | ForEach-Object { 'src/test/java/' + $_.Replace('.', '/') + '.java' })

& $javac --release 8 -encoding UTF-8 -cp $dependencyClasspath -d $classes @sources
if ($LASTEXITCODE -ne 0) { throw 'Application compilation failed.' }
& $javac --release 8 -encoding UTF-8 -cp "$classes;$dependencyClasspath" -d $testClasses @testFiles
if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed.' }
foreach ($case in $cases.GetEnumerator()) {
    $name = $case.Key
    $expectedCount = $case.Value
    $output = & $java '-Djava.awt.headless=true' "-Duser.home=$testHome" -cp "$testClasses;$classes;$dependencyClasspath" junit.textui.TestRunner $name
    $exit = $LASTEXITCODE
    $output | Write-Output
    if ($exit -ne 0) { throw "Test failed in $name (exit $exit)." }
    if (($output -join "`n") -notmatch "(?m)^OK \($expectedCount tests?\)\r?$") {
        throw "Unexpected test result in $name. Expected $expectedCount passing case(s)."
    }
    Write-Output "$name passed $expectedCount case(s)."
}
```

## Report

Send the lead the checkout revision, pinned baseline, compiler and runtime versions, and result for each class. On failure, include the command, exit code, and relevant output. The lead owns decisions about changes to the code or tests. Packaged startup and real-desktop QA are separate stage 1 checks.
