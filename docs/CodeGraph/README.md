# Graf kodu jFTP

Otwórz [interaktywny graf](graph.html) w przeglądarce. W polu wyszukiwania wpisz `FTPSession` albo `JFTPApplication`. Kliknij wynik, żeby przejść do węzła i zobaczyć jego relacje. Graf korzysta z lokalnej kopii biblioteki vis-network `9.1.6`; przy przenoszeniu pliku HTML zachowaj katalog `.artifacts`.

[Surowy graf JSON](graph.json) ma 2245 węzłów, 7297 relacji, w tym 1915 relacji `calls`, oraz 108 grup. Graphify `0.9.91` przeanalizował w trybie `code-only` 182 pliki w `src/main/java` przy czystej rewizji `a3f67544d2102a3454d8eef14cd146b1ecf25a73`. Dla trzech plików `package-info.java` powstały izolowane węzły plików bez wydobytych symboli. Implementacja zewnętrznej biblioteki FTP jest poza zakresem.

Powiązania klas z pakietami językowymi, JavaHelp, obrazami i konfiguracją Maven są w osobnym [grafie zasobów i pakowania](../jftp-zasoby.md#powiązania-kodu-i-pakowania). Diagram opisuje odczyt i pakowanie plików, a graf Graphify pokazuje relacje kodu Javy.

## Relacje sprawdzone w źródłach

- Trzy krawędzie przychodzące do `FTPSession.connect(RemoteHost)` odpowiadają wywołaniom w [ConnectAction](../../src/main/java/com/myjavaworld/jftp/actions/ConnectAction.java#L73), [FavoritesDlg](../../src/main/java/com/myjavaworld/jftp/FavoritesDlg.java#L249) i [FTPSession.reconnect](../../src/main/java/com/myjavaworld/jftp/FTPSession.java#L703).
- Graf pokazuje wywołanie `JFTP.newSession()` z [konstruktora JFTPApplication](../../src/main/java/com/myjavaworld/jftp/JFTPApplication.java#L62). Ten sam konstruktor tworzy okno przez [`new JFTP()`](../../src/main/java/com/myjavaworld/jftp/JFTPApplication.java#L55), ale Graphify pominął tę relację.
- Graphify błędnie połączył [`new SwingWorker()` w `connect`](../../src/main/java/com/myjavaworld/jftp/FTPSession.java#L617) z innym obiektem [`SwingWorker` w `disconnect`](../../src/main/java/com/myjavaworld/jftp/FTPSession.java#L1500). Sprawdzaj krawędzie w kodzie przed zmianą sygnatury lub usunięciem klasy. [Cytaty i hasze źródeł](.artifacts/graph-evidence.json)

## Odtworzenie analizy

Po przygotowaniu `uvx` i pobraniu `graphifyy==0.9.91`, uruchom te polecenia w PowerShellu w katalogu głównym repozytorium. Analiza działa lokalnie bez kompilacji, etapu analizy dokumentów i etykietowania przez model.

```powershell
$graphStage = Join-Path ([System.IO.Path]::GetTempPath()) ("jftp-graph-" + [guid]::NewGuid())
$graphJson = Join-Path $graphStage 'graphify-out/graph.json'
uvx --offline --from graphifyy==0.9.91 --python 3.13 graphify extract src/main/java --code-only --force --out $graphStage
uvx --offline --from graphifyy==0.9.91 --python 3.13 graphify cluster-only $graphStage --graph $graphJson --no-label --no-viz
uvx --offline --from graphifyy==0.9.91 --python 3.13 graphify export html --graph $graphJson --node-limit 3000
```

Przy publikacji bieżącego HTML zamieniono odwołanie do CDN na lokalny plik `.artifacts/vis-network.min.js`, którego SHA-384 sprawdzono względem sumy zapisanej przez Graphify. Przy odświeżaniu grafu zachowaj ten lokalny odnośnik. [Licencja MIT biblioteki](.artifacts/vis-network-LICENSE-MIT.txt)
