# Characterization and regression tests

The JUnit 3 tests cover local filtering, connection profiles, ZIP round trips, Swing worker completion, and popup row selection. They use synthetic files and a headless Swing test. A real desktop session and FTP server are outside this suite.

`MTableTest` captures the selection the popup listener sees for an already selected row, a different row, and the area below all rows. When the pre-fix Mac branch from the parent of `14e62ce` was selected in a staging copy on Windows, the last two cases failed with the original selection still present. This checks the regression without claiming native macOS verification.

## Run on the current Windows baseline

Stock `mvn test` stops at the historical HTTP repository. The following path compiles the unchanged application and tests directly. Use a JDK whose `javac` supports `--release 8`. Set `JFTP_TEST_DEPS` to a local folder containing [ftpapi 3.0.0](https://www.jmethods.com/ftpapi/download.html), [JavaHelp 2.0.05](https://repo.maven.apache.org/maven2/javax/help/javahelp/2.0.05/), [JUnit 3.8.1](https://repo.maven.apache.org/maven2/junit/junit/3.8.1/), and [AppleJavaExtensions 1.4](https://repo.maven.apache.org/maven2/com/apple/AppleJavaExtensions/1.4/) for the legacy Mac adapter. Run from the repository root.

```powershell
$testRoot = Join-Path ([IO.Path]::GetTempPath()) ('jftp-tests-' + [guid]::NewGuid())
$classes = Join-Path $testRoot 'classes'
$testClasses = Join-Path $testRoot 'test-classes'
$testHome = Join-Path $testRoot 'home'
New-Item -ItemType Directory -Path $classes,$testClasses,$testHome | Out-Null
$dependencies = Join-Path $env:JFTP_TEST_DEPS '*'
$sources = @(git ls-files -- 'src/main/java/*.java')
$tests = @(Get-ChildItem src/test/java -Recurse -File -Filter '*Test.java')
$testFiles = @($tests | ForEach-Object FullName)

javac --release 8 -encoding UTF-8 -cp $dependencies -d $classes @sources
if ($LASTEXITCODE -ne 0) { throw 'Application compilation failed.' }
javac --release 8 -encoding UTF-8 -cp "$classes;$dependencies" -d $testClasses @testFiles
if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed.' }

$testSourceRoot = (Resolve-Path 'src/test/java').Path
foreach ($test in $tests) {
    $name = $test.FullName.Substring($testSourceRoot.Length + 1).Replace('\', '.').Replace('.java', '')
    java '-Djava.awt.headless=true' "-Duser.home=$testHome" -cp "$testClasses;$classes;$dependencies" junit.textui.TestRunner $name
    if ($LASTEXITCODE -ne 0) { throw "Test failed in $name." }
}
```
