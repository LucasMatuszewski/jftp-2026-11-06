# Headless OpenCode exploration

Use this for optional read-only exploration when provider use and the exact input scope are authorized. Check the installed `opencode --version` and `opencode run --help`. Confirm local CLI flags and provider details before reuse. Run any initial connectivity check with a synthetic Markdown brief after usage is authorized. [OpenCode CLI](https://opencode.ai/docs/cli/) · [Xiaomi integration](https://mimo.mi.com/docs/en-US/tokenplan/integration/opencode)

Put a dedicated `opencode.json` outside the checkout and pass its path through `OPENCODE_CONFIG`. Make `MIMO_API_KEY` available in the process environment. Use a separate scratch directory containing only a copy of the brief and any explicitly authorized inputs. Use scratch-relative paths in the brief and keep credentials out of it. The example uses the Xiaomi pay-as-you-go endpoint. For a token plan, use the endpoint for the authorized account. [OpenCode configuration](https://opencode.ai/docs/config/) · [OpenCode permissions](https://opencode.ai/docs/permissions/)

```json
{
  "$schema": "https://opencode.ai/config.json",
  "provider": {
    "mimo": {
      "npm": "@ai-sdk/openai-compatible",
      "options": {
        "baseURL": "https://api.xiaomimimo.com/v1",
        "apiKey": "{env:MIMO_API_KEY}"
      },
      "models": {
        "mimo-v2.6-pro": { "name": "mimo-v2.6-pro" }
      }
    }
  },
  "permission": {
    "*": "deny",
    "read": "allow",
    "glob": "allow",
    "grep": "allow",
    "list": "allow",
    "bash": "deny",
    "edit": "deny",
    "task": "deny",
    "external_directory": "deny"
  }
}
```

PowerShell example for OpenCode 2.2.5. Set `$openCodeScratch` and `$openCodeConfig` to the absolute paths of the dedicated directory and configuration file. Copy the authorized brief to `brief.md` in scratch.

```powershell
if ([string]::IsNullOrWhiteSpace($env:MIMO_API_KEY)) {
    throw 'MIMO_API_KEY must be set in the process environment.'
}

$openCodeBrief = Join-Path -Path $openCodeScratch -ChildPath 'brief.md'
if (-not (Test-Path -LiteralPath $openCodeBrief -PathType Leaf)) {
    throw 'Copy the authorized task brief to the OpenCode scratch directory first.'
}

$previousOpenCodeConfig = $env:OPENCODE_CONFIG
Push-Location -LiteralPath $openCodeScratch -ErrorAction Stop
try {
    $env:OPENCODE_CONFIG = $openCodeConfig
    opencode run --standalone --model mimo/mimo-v2.6-pro --agent plan --format json --file $openCodeBrief 'Follow the attached brief.md and report your findings with evidence.'
    if ($LASTEXITCODE -ne 0) {
        throw "OpenCode exited with code $LASTEXITCODE."
    }
} finally {
    $env:OPENCODE_CONFIG = $previousOpenCodeConfig
    Pop-Location
}
```

Check the output for errors and for the evidence requested in the brief. Verify findings against the original sources. Remove the scratch copies after accepting the report. If access is unavailable, use an authorized internal worker.
