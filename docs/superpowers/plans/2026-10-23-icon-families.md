# Native historical icon families Implementation Plan

> Execute inline using executing-plans. The user explicitly requests autonomous implementation; no design approval pause. Reuse the existing feature checkout and preserve the previous OpenCode brief. No external delegation in this phase.

**Goal:** Six original icon families,32 functions each, native rendering and an accessible comparison gallery without sacrificing app identity.
**Architecture:** A deterministic vector-art generator emits SVG masters, Android VectorDrawable resources, a manifest and a compile-time resource catalog. An exact package catalog replaces broad substring inference. Existing AppIcon precedence remains authoritative; personal replacements, external packs and imported artwork retain priority. Modern unmatched apps preserve their actual symbol.
**Tech Stack:** Python standard library authoring, Kotlin/Compose, existing Android toolchain. No runtime SVG dependency.
**Spec:** docs/OPENCODE-ICON-PACKS.md, adapted to direct implementation by Codex at the user's request.

## Constraints and rulings

- No new automated tests or suite runs; use source inspection, build/lint and visual artifact review. Build success is not physical performance proof.
- Do not claim these original reinterpretations are authentic manufacturer assets. No personal artwork is bundled or uploaded.
- One family may share construction primitives, but each theme must change object/material/geometry and not only tint. Core eight functions receive explicit theme-specific treatment.
-192 means32 functional variants times6 themes, not192 apps covered. Unknown and modern apps keep their identity. Exact system-app mappings replace fuzzy package matching.
- No settings schema change, permission or new idle animation. Normal and monochrome choices remain usable.
- Existing session checkout contains only the prior instruction document; preserve it. Build helper paths depend on this checkout, so reuse rather than move it.

## Tasks

- [x] Create reproducible original vector masters/resources and compile-time catalog. Render a contact sheet and inspect core designs.
- [x] Integrate exact package matching and native family rendering with existing precedence; remove superseded fuzzy rendering.
- [x] Add32-function gallery to the theme editor with theme switching, real vector resources, legible labels and no preference writes.
- [x] Review source, compile optimized beta/lint, document evidence and physical limitations.
- [ ] Sign the update with the existing certificate, package source and install/copy to the known Motorola if reachable.

## Review focus

Normal icons bypass all family treatments. Explicit MONOCHROME cannot silently become a colored vector. Unknown packages, substring collisions, imported/custom artwork and external packs must retain correct precedence. Native vector gradients must render correctly at32/42/60dp. Gallery resizing must not intercept Home gestures or write settings. Record any device-unavailable gates as pending.

Delivery: signed beta26 and source artifacts ready. Device installation/Downloads copy not confirmed after ADB EOF failures and loss of the final raw-transfer process handle. Native visual checks remain pending; see docs/QA-beta26.md.
