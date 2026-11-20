# Prepare the Windows FTP and FTPS test server

Plan: docs/modernization-plan.md

Task: 04-prepare-loopback

Application baseline: `8fa8c7fad726f8c3a5f3e4499c50dd5f507d35b4` plus the launcher candidate. The app is outside this assignment.

## Scope and prerequisites

Use the isolated directory `C:/Users/labuser/AppData/Local/Temp/jftp-modernization-373926d3-9bb2-4c0d-ac99-2325d14367a8`. Own only its new `loopback-fixture/` subtree. The sibling `loopback-tools/` contains pyftpdlib 2.2.0 and pyOpenSSL 26.4.0. Use Python 3.14 and `C:/Program Files/Git/usr/bin/openssl.exe`. No changes to tracked files, OS trust, firewall, live accounts, or other application state. Use only data generated for this test. The installed dependencies and network socket may require the normal command approval path; stop and report a denial.

## Checklist

1. Write `loopback-fixture/server.py` for real FTP and explicit FTPS on IPv4 `127.0.0.1`. Give it a documented invocation and a way to print the two selected high control ports (use ephemeral ports or verify availability before binding). Use sibling scratch subdirectories as server roots, with a distinct starting directory and marker file for each endpoint. Keep data connections on loopback. Use a designated synthetic account with read, list and write permission. Leave anonymous access disabled. Generate a test-only PEM certificate and key with an IP SAN for `127.0.0.1`, and enforce TLS on both control and data channels on FTPS. Keep the private key in this scratch directory.
2. Make a second, separate plain-FTP root available for the abort case, on its own high control port. Cap upload speed so the first file in a two-item batch stays in progress long enough to abort. Preserve incomplete files and log transfer outcomes so a later verifier can distinguish an incomplete first item and an absent second item. Ensure the ordinary FTP/FTPS cases remain fast.
3. With the server active, use Python's `ftplib.FTP` and `FTP_TLS` over loopback to check wrong-password refusal, login, each starting directory, binary upload and download, and certificate verification with the test PEM as the reference client's CA. Call `prot_p()` for secure data and verify a listing and transferred bytes. Use only the assigned roots for all writes. Use evidence from the real server, including its logs and real file hashes. For the throttled endpoint, demonstrate measurable progress on a synthetic upload before stopping that fixture process or client.
4. Stop the fixture servers using their specific process handles. Verify all ports are closed. Leave the scripts, certificate, and evidence files in the fixture directory so the lead can restart the real service. Do not drive the jFTP UI or change app sources.

## Report

Report the revision, exact runnable commands, actual endpoints, service logs, certificate subject/SAN, account setup location, and which checks passed. Include elapsed transfer time, file hashes, refusal reply, and shutdown evidence. Identify failed or unverified checks with their error outputs. Keep real credentials and private keys out of tracked files. Use the official [pyftpdlib API](https://pyftpdlib.readthedocs.io/en/latest/api.html) and installed sources when checking behavior.
