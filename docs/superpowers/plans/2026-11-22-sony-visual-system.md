# Sony Visual System Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: use `executing-plans` after this plan is approved.

## Intent

Apply the five approved visual improvements as one restrained Sony design system while retaining Aura's vertical list, search, A–Z, widgets and user content.

## Design decisions

- **Focus:** one palette-aware optical source drives focused app rows, panels and Quick Center selection. It uses border, soft halo and a short confirmation pulse, not independent glows.
- **Depth:** keep current wallpaper ownership. Add inexpensive draw-time depth only: base gradient, retained waves/parallax and sparse static/interaction-driven particles. No always-running composition loop.
- **Type:** use existing bundled Noto alternative and tune Classic/PS5/Vita variants to a strict HUD/title/body/label hierarchy.
- **Materials:** Classic uses blue translucent wave glass; Midnight uses dark elevated quiet panels and pale rim light; White/Vita uses electric cyan gloss with bounded grid/bubble detail.
- **Continuity:** expansion origin supplies color, rim and elevation to the destination, and restores them on close. Motion is disabled for reduce-motion, power saver, high contrast and animation-disabled environments.

## Files

- `app/src/main/java/dev/senda/launcher/ui/theme/Ps4PaletteColors.kt`: palette-specific visual token data.
- `app/src/main/java/dev/senda/launcher/ui/theme/HistoricalDesign.kt`: shared console material/focus treatment.
- `app/src/main/java/dev/senda/launcher/ui/Ps4Theme.kt`: focused app-row visual connection.
- `app/src/main/java/dev/senda/launcher/ui/QuickCenter.kt`: align panel/rail selected light with shared focus source.
- `app/src/main/java/dev/senda/launcher/ui/theme/ThemeTypography.kt`: strict three-variant Sony hierarchy.
- `app/build.gradle.kts`, `README.md`, `CHANGELOG.md`, `docs/CONTINUITY.md`, `docs/BETA114.md`, `docs/coordination/*`: version and evidence.

## Tasks

1. Define palette-specific material/focus tokens and consume them from existing panel/focus modifiers.
2. Refine PS4 background depth and selection surfaces with cached drawing only; no perpetual recomposition.
3. Tune existing typography variants and preserve user font overrides.
4. Apply shared focus continuity to app rows and Quick Center, retaining all semantics and 48dp interactions.
5. Incorporate the read-only Antigravity review; compile with `assembleBeta lintBeta`, inspect source/diff, and document actual results. Do not install without fresh device and signing verification.
