# Primary validation — Sonnet and GPT-OSS via Antigravity

26 October2026. App remains beta38. Delegated analysis only; no Android source/APK changes.

Both pinned models returned READY and completed real review responses: claude-sonnet-4-6 (preview), gpt-oss-120b-medium (visual). The earlier RESOURCE_EXHAUSTED result was for the prior default model; it did not mean all Antigravity models were exhausted. No billing change or account switch.

## Execution boundaries

Only the seven explicitly approved UI source files were eligible; each task received its own subset, recorded with hashes/ranges in execution.json. Isolated temporary working directories, no copied credentials/backups/device evidence. First plan-mode runs produced a plan/intermediate answer, not accepted reviews. Final runs used an explicit custom review role and direct task response.

AGY1.4.7 advertised its tools even with tools: []; therefore this must not be described as an enforced no-tools sandbox. The actual final stream step types were only user_input and agent_response: no tool execution was observed. Runner now checks this and rejects unexpected action steps. It does not grant global trust or bypass permissions.

## Sonnet review decisions

1. ACCEPT: preview rail uses railPortrait unconditionally (ThemeLivePreview.kt:85), whereas HomeScreen.kt:508 selects railLandscape for compact orientation or fontScale>=1.4. Share selection logic in a future bounded fix; verify both orientations and large text. Not yet implemented here.
2. DOCUMENTED LIMITATION: default-HOME permission/status/error banners are not part of the preview. Do not add permission actions there; clarify the preview assumes normal Home content. No fixed guessed banner height accepted.
3. ACCEPT GOAL / REJECT SAMPLE PATCH: inspection cannot scroll because all pointer events are consumed. A safe inspection mode is useful; the suggested positionChange filter can consume initial down and break drag recognition, so it is not implementation-ready. Preserve passive widgets and inert actions.
4. REJECT: Ps4Wallpaper is a Canvas background, not the rejected console navigation. It receives reduced=true in preview; ThemeMotion.kt:30 returns after snapTo when reduced. Removing the background would reduce visual fidelity with no demonstrated defect.
5. REJECT AS APP DEFECT: cited garbled WidgetShelf strings are correct UTF-8 in the actual file. Whole-file CP1252 fallback in the context builder corrupted valid UTF-8 spans when encountering one legacy byte elsewhere. Runner now preserves valid UTF-8 and decodes only isolated invalid bytes as CP1252. No automatic conversion of production files.

## GPT-OSS review decisions

1. REJECT: changing Roboto ICS to SamsungOne is unsupported by supplied historical evidence and conflicts with retaining the SIII-era reference. No verified source was provided.
2. REJECT: IconHolder.CIRCLE token alone does not prove Nothing renders a default circle. HomeScreen.kt:1145-1154 and FamilyArtwork.kt gate holders through customHolder/bareNothing; preserve the user's no-circle default.
3. REJECT: always drawing PS4 wallpaper under high contrast would defeat the intentional solid high-contrast treatment. Do not change accessibility for decorative fidelity.
4. OPTIONAL REFACTOR, NOT A BUG: typed font enum could help maintainability, but ThemeSnapshot.validated already bounds JSON strings against fontChoices. No unsolicited unit suite or schema churn.
5. DEFER: no evidence from supplied excerpts establishes broken timing propagation across all UI. ThemeMotion.kt already varies periods by theme. Do not impose one duration on every interaction.

## Recommended next code work

First: shared preview/Home rail sizing and a separately designed safe inspection mode. Then portable Room variant backup as its own approved task. Historical icon work still needs verified references and manual optical acceptance. No new effects to main before physical performance evidence. Raw model text is preserved separately and must never be treated as an approved patch.
