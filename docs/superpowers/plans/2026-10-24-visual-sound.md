# Aura visual identity and sound Implementation Plan

> Execute inline with executing-plans. User authorized autonomous work and new useful features, without routine questions. Keep the current checkout and do not delegate unless later requested. UI/UX Pro Max governs hierarchy, contrast, stable interaction and coherent families.

**Goal:** Refine the entire visual identity of six themes and add original, controllable sound feedback without compromising daily use, personal artwork or data.
**Architecture:** Shared historical material roles feed search, alphabet, cards and theme studio. A small native SoundPool controller plays generated local PCM assets only for explicit interactions and user previews. Independent settings flow persists audio preferences and backup10 accepts formats1–10. Native cached wallpaper scenes and reversible per-theme/readability settings follow as separate reviewable milestones.
**Tech Stack:** Existing Kotlin/Compose/Android framework; Python standard library for original sound authoring. No network/runtime graphics dependency or service for sound.
**Spec:** This document and prior docs/HISTORICAL-THEMES.md, docs/OPENCODE-ICON-PACKS.md, docs/BETA26.md. User explicitly approves new visual/audio refinement.

## Time and quota

Deadline interpreted literally: tomorrow25 October2026 at06:00 America/Santiago, verified09:00 UTC. Heartbeat automation continuar-mejoras-de-aura is active every30min to resume unfinished milestones. It must pause at deadline or when all approved milestones are complete, and remain quiet without material changes. This does not guarantee uninterrupted execution if the desktop cannot run.

Account starts62% short window/25% weekly. Check at milestone boundaries; checkpoint and pause the automation at90–92% before the human95% cap. Do not artificially exhaust quota. The earlier blocked goal's unavailable phone is not a dependency for code/artifact improvements.

## Invariants

- Preserve favorites, widgets, normal icons, individual overrides, external packs, personal collections and manually chosen wallpapers. No uninstall, new permissions or account changes.
- No new automated suites or suite runs without explicit request; compile/lint, source and visual review with honest limits.
- No manufacturer audio/assets embedded. New sounds are original synthesized short cues, not ringtones or recorded samples.
- Sound defaultOFF, independent volume and event switches, selected/automatic family. Respect silent/vibrate, Do not disturb and system touch-sound preference; optionally mute during media (defaultON). No forced stream volume, no recording and no background playback.
- Sounds only on actual actions, never recomposition/timers, clock/weather refresh or every app-list frame. Alphabet audio defaultOFF with rate limit. Release pool/callbacks on stop/destroy. Preview requires an explicit press and obeys system policy.
- Preserve functional placement; distinguish themes with material, shape, type, borders and original scene. No idle animated wallpaper or expensive full-screen blur.
- Preview/browse does not apply preferences. State writes happen only on explicit controls; per-theme editor retains Apply/Cancel.

## Milestones

- [x] M1: original sound generator/catalog, lifecycle-safe playback, persisted controls/backup10, event hooks and settings. Compile/lint, source audit, checkpoint.
- [ ] M2: themed search surface and glow, alphabet track/indicator/bubble, section headings and consistent cards. Contrast and large text review; no gesture regression. Native original wallpaper scenes with cached geometry and no idle timer.
- [ ] M3: reversible readability/custom wallpaper controls and a fuller theme comparison studio using production components. Preserve chosen resources. Add accessible letter-jump alternative if it improves the current small rail targets.
- [ ] M4: inspect all six themes at relevant sizes/modes, source audit of sound lifecycle/policy and state persistence, final optimized build/lint/signing/source hashes, document remaining device checks and install/copy only if authorized Motorola is available.
- [ ] Close: before deadline/quota cap, produce concise report and pause heartbeat; do not add filler work after the approved milestones are complete.

## Review focus

Silent/DND/system touch-sound disabled and media playback must suppress sound; disabled settings allocate no persistent audio resources. Loading callbacks cannot play after leaving Home or after a new preview supersedes them. Rapid rail scrubbing must be rate-limited independently of other actions. Old backup imports default safely, new settings are bounded and validated. New themed surfaces must retain keyboard/input semantics, empty-space lock gestures, icon choices and contrast. No claim of runtime fidelity/FPS/native audio verification from a build or source render alone.

## Updated priority during execution

Installed-app pack coverage now takes precedence over further generic UI embellishment; see2026-10-24-installed-app-icon-packs.md. M2/M3 code is implemented and compile-checked; visual/physical acceptance remains part ofM4. Keep open checkboxes until that review is recorded. The user provided the new phone endpoint and the176-app inventory is captured; do not repeat stale disconnected-device requests. Continue per-app historical optical refinement after the current beta checkpoint.
