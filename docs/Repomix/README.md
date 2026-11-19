# Repomix dla jFTP

[Pakiet XML](jftp-repomix.xml) obejmuje 182 śledzone pliki `.java` z `src/main/java`. Zawiera deklaracje, ciała metod i oryginalne komentarze. `pom.xml`, zasoby, pomoc i pozostałe katalogi są poza zakresem pakietu.

| Plik | Co zawiera | Rozmiar |
| --- | --- | --- |
| [RepoMapa](../jftp-repo-map.rust.md) | Ścieżki, numery linii i 243 wybrane deklaracje z 1586 wykrytych | 4096 szacowanych tokenów w surowej mapie, według liczby znaków podzielonej przez 4 |
| [Repomix](jftp-repomix.xml) | Kod źródłowy ze wszystkich 182 plików w tym samym zakresie | 228 324 tokeny według `o200k_base`, 897 787 bajtów |
| [Repomix skompresowany](jftp-repomix.compressed.xml) | Wybrane fragmenty kodu z tych samych 182 plików, z usuniętymi komentarzami | 96 323 tokeny według `o200k_base`, 392 501 bajtów |

RepoMapa wskazuje klasę `FTPSession` i wybrane metody. W pakiecie wyszukaj `<file path="src/main/java/com/myjavaworld/jftp/FTPSession.java">`, a następnie `connect(final RemoteHost remoteHost)`. Zobaczysz między innymi tworzenie klienta FTP i parsera listingu przez `Class.forName`. Do weryfikacji zachowania i cytowania numerów linii korzystaj z [oryginału](../../src/main/java/com/myjavaworld/jftp/FTPSession.java#L593). Przy pytaniu o jedną metodę wystarczy odczytać jej fragment.

## Weryfikacja i odtworzenie

Pakiet wygenerowano narzędziem Repomix `1.18.1` przy rewizji `402e821cfb405dd721c34c3c9be2bc0a9697c7c4`. W badanym katalogu ta rewizja ma ten sam kod co rewizja `14e62ce` zapisana w RepoMapie. Weryfikacja XML potwierdziła kompletność listy plików i zgodność treści po normalizacji końców linii. W 96 wpisach Repomix przyciął białe znaki na brzegach pliku. Kompresja strukturalna i usuwanie komentarzy były wyłączone. Sumy SHA-256 źródeł przed i po pakowaniu były identyczne.

- SHA-256 pakietu `8ad07d206cad6a43d14e5c04f386aeed0ba1380fc21f1bbe8dd6e1f4383afe60`
- SHA-256 [konfiguracji](.artifacts/repomix.config.json) `0466341aa6de32fbeb618ab9e324c604a64c2b75faf86cd159cdca5a8a8948cc`

W PowerShellu, z katalogu głównego repozytorium:

```powershell
$repomixStage = Join-Path ([System.IO.Path]::GetTempPath()) ("jftp-repomix-" + [guid]::NewGuid())
New-Item -ItemType Directory -Path $repomixStage | Out-Null
$repomixOutput = Join-Path $repomixStage 'jftp-repomix.xml'
npx.cmd --yes repomix@1.18.1 . --config docs/Repomix/.artifacts/repomix.config.json --output $repomixOutput
if ($LASTEXITCODE -ne 0) { throw 'Generowanie Repomix zakończyło się błędem.' }
Copy-Item -LiteralPath $repomixOutput -Destination docs/Repomix/jftp-repomix.xml
```

## Wariant skompresowany bez komentarzy

Wariant zajmuje o 57,81% mniej tokenów niż pełny pakiet. Kompresja strukturalna pomija fragmenty implementacji. Weryfikacja potwierdziła tę samą listę 182 plików oraz brak znaczników komentarzy Java w treści wpisów XML. Repomix `1.18.1` zachował jeden Javadoc mimo włączonego usuwania komentarzy, dlatego [konfiguracja wariantu](.artifacts/repomix.compressed.config.json) używa [procesora wejścia](.artifacts/remove-retained-javadoc.mjs), który usuwa ten konkretny komentarz z kopii `JFTPPreferences.java` używanej do pakowania.

- SHA-256 pakietu `ad5d472c9358781a0e040729a5bed37fdb4086b11999b2edc852433eaf2c510d`
- SHA-256 konfiguracji `0a2a635550038ba8c037af0869909f5c7478b0176705ed03ed6b6801fe5011a2`
- SHA-256 procesora wejścia `18c4e03f4187312f98867567e03413791ae7256fb24ec143e2b118be5a77917b`

Po utworzeniu `$repomixStage` jak w przykładzie powyżej, uruchom w katalogu głównym repozytorium:

```powershell
$repomixOutput = Join-Path $repomixStage 'jftp-repomix.compressed.xml'
npx.cmd --yes repomix@1.18.1 . --config docs/Repomix/.artifacts/repomix.compressed.config.json --output $repomixOutput
if ($LASTEXITCODE -ne 0) { throw 'Generowanie skompresowanego Repomix zakończyło się błędem.' }
Copy-Item -LiteralPath $repomixOutput -Destination docs/Repomix/jftp-repomix.compressed.xml
```
