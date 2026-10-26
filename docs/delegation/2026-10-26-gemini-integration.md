# Gemini integration — beta38

Gemini CLI0.65.0 installed through official npm package @google/gemini-cli. Version/help verified. No model response or delegation completed yet: Google authentication is pending in the official browser flow opened for the user.

The user explicitly requested Gemini integration and task delegation. Source sharing is restricted to code required by each task. Keys, credentials, personal backups and private device evidence are excluded. No paid provider or billing change configured.

## Responsibilities

- Gemini visual task: review differentiation of theme tokens, typography and components; five evidence-backed findings. Historical fidelity cannot be certified from code alone.
- Gemini preview task: compare production/preview geometry, large text, orientation, scroll and widget/action isolation; propose bounded corrections.
- Codex: select context, validate findings against actual source, reject unsupported conclusions, own implementation/build/lint/device acceptance and integration.

## Reproducible dispatch

Complete Google sign-in through the official CLI flow first. No passwords/tokens through chat.

`python tools/gemini/delegate-review.py visual`

`python tools/gemini/delegate-review.py preview`

The runner supplies numbered excerpts through stdin and an explicit CLI policy denying all tools. No project trust is required. Each task has a240-second timeout and stores a source hash/range manifest and raw response outside Git, in work/gemini-aura-review. No auto-retry or automatic integration. The default authenticated Google model is used; its actual returned model/usage must be recorded after execution, not guessed.

Policy/assignments: docs/delegation/gemini/review.toml, visual.md, preview.md. GEMINI.md establishes project boundaries for future human/agent use.

## Approval review and validation

Automatic review rejected selecting Trust folder because it could enable persistent hooks/MCP/configuration. Chose Don't trust and continued to the official Google login. Trust remains disabled; no bypass attempted.

Verified: CLI installation/version/help, authentication-required response, task files, Python syntax compilation. No Android app changes or suites executed. Beta38 remains installed. Phase2 not started. Integration is prepared, not authenticated/operational until user finishes Google sign-in and a real task succeeds.

Official documentation: https://geminicli.com/docs/get-started/authentication/ ; https://geminicli.com/docs/reference/policy-engine/
