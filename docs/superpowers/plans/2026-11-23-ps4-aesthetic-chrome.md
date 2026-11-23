# PS4 Aesthetic Chrome Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `executing-plans` task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver the approved PS4 HUD, cached wave depth, glass Quick Center and unified selection focus while retaining Aura's vertical launcher ergonomics.

**Architecture:** Reuse the current `Ps4PaletteColors`, PS4 material modifiers and system-motion guards. Draw only cached static geometry and finite state-driven transitions; connect HUD, wallpaper, Quick Center and list focus through existing palette/focus state rather than creating new global animation loops.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, existing Aura palette/motion helpers.

**Spec:** `docs/superpowers/specs/2026-11-23-ps4-aesthetic-chrome-design.md`

## Global Constraints

- Keep Aura's vertical list, search, A–Z, widgets, one-tap launch and personal preferences.
- No new permission, dependency, test suite, media behavior or console carousel.
- Respect reduce motion, high contrast, preview, power saver and system animation scale.
- Validate only with `assembleBeta lintBeta`, static review and visual-source review.

## Review Focus

- Idle PS4 surface must not launch a perpetual frame loop or rebuild wave geometry.
- High contrast and reduced-motion states must retain readable HUD, controls and focus without decorative animation.
- Midnight, Classic and Vita must remain visually distinct but use the same semantic focus source.
- Quick Center's touch target and TalkBack label must remain unchanged.
- Search/A–Z/list scrolling must retain current layout and input behavior.

---

### Task 1: Shared visual token refinement

**Files:**
- Modify: `app/src/main/java/dev/senda/launcher/ui/theme/Ps4PaletteColors.kt`
- Modify: `app/src/main/java/dev/senda/launcher/ui/Ps4HeaderSelection.kt`

- [ ] Add minimal palette tokens for upper reflection and finite focus sweep; retain existing token values as fallback.
- [ ] Make the app-row focus halo and reflected edge consume these shared tokens without allocating per frame.
- [ ] Source-review suppressions for high contrast and zero intensity.

### Task 2: HUD and retained PS4 depth

**Files:**
- Modify: `app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt`
- Modify: `app/src/main/java/dev/senda/launcher/ui/Ps4LiveWallpaper.kt`

- [ ] Apply PS4 HUD hierarchy/reflection using existing header elements and palette tokens.
- [ ] Add only cached depth/reflection drawing and finite interaction-linked transform to the existing wallpaper.
- [ ] Verify no state clock, polling or idle particle loop is introduced.

### Task 3: Quick Center glass surface

**Files:**
- Modify: `app/src/main/java/dev/senda/launcher/ui/QuickCenter.kt`
- Modify: `app/src/main/java/dev/senda/launcher/ui/Ps4Material.kt`

- [ ] Refine panel and button glass with palette-specific top reflection, edge and readable opacity.
- [ ] Reuse the shared focus token on selected controls; preserve existing semantics and target sizes.
- [ ] Source-review motion guards and contrast fallback.

### Task 4: Version, evidence and validation

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `README.md`
- Modify: `CHANGELOG.md`
- Modify: `docs/CONTINUITY.md`
- Create: `docs/BETA115.md`
- Modify: `docs/coordination/README.md`
- Modify: `docs/coordination/STATE.json`

- [ ] Set beta115 version metadata.
- [ ] Run `gradlew.bat assembleBeta lintBeta` with the established JDK.
- [ ] Record only observed build/lint evidence, source-review findings and remaining physical-validation limits.
