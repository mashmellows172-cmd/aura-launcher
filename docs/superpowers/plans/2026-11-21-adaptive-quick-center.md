# Adaptive Quick Center Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: use `executing-plans` after this plan is accepted.

## Intent

Make the one-row Quick Center choose the most relevant contextual capsule without removing the user's persistent tools. Keep Aura's vertical-launcher ergonomics, explicit controls and accessibility semantics.

## Approved approach

Derive a transient display priority from existing `ActiveAudioInfo`, alarm and agenda inputs. The stored order remains authoritative; no automatic preference writes occur. `Accesos` remains a stable rail item and keeps Linterna, Calculadora and Gemini in its fixed internal order. Media controls are revealed only for capabilities supplied by Android's MediaSession. A small daily scene is synthesized from the next relevant alarm/event/media state and is omitted if it has no useful content.

## Non-goals

- No embedded YouTube/video surface or fabricated queue/device controls.
- No new permissions, background polling, dependency, automatic test suite or permanent reorder.
- No changes to launcher data, icon packs, replacements or user settings.

## Files

- `app/src/main/java/dev/senda/launcher/ui/QuickCenter.kt`: transient prioritization, daily scene, capability disclosure and integration.
- `app/src/main/java/dev/senda/launcher/ui/QuickCenterActions.kt`: explicit fixed-tool wording/order only if required by the integration.
- `app/build.gradle.kts`: beta113 version.
- `CHANGELOG.md`, `docs/BETA113.md`, `docs/CONTINUITY.md`, `docs/coordination/README.md`, `docs/coordination/STATE.json`: delivery evidence and limits.

## Tasks

1. Add pure local priority helpers that rank the already-configured cards using only existing state, leaving stored order intact. Show a compact daily scene when audio, alarm or agenda provides useful immediate context.
2. Keep `Accesos` in the rail and render fixed Linterna, Calculadora and Gemini controls when revealed. Surface media capability status and only mount supported actions in the expanded media card.
3. Increment beta to 113; update delivery documents honestly.
4. Run `gradlew.bat assembleBeta lintBeta`; inspect changed source and build output. Do not install or claim device validation unless ADB/certificate/version are independently verified.
