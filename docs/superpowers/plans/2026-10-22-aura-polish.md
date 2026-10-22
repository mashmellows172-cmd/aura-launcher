# Aura Polish Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Deliver a more robust, customizable and visually coherent Aura beta with a classic Motorola theme.

**Architecture:** Preserve the Compose app and current stores. Extract pure decision logic for tests, keep image/ZIP IO isolated, and layer a draft editor over existing dashboard settings. Theme changes continue to affect appearance rather than layout.

**Tech Stack:** Kotlin 2.2.10, AGP 9.2.1, Compose BOM 2025.05.00, Android API29–37, JDK17, JUnit4.

**Spec:** `docs/superpowers/specs/2026-10-22-aura-polish-design.md`.

## Global Constraints

- Preserve the existing package and signing certificate; update without uninstalling.
- Keep Nothing/CMF principal, manufacturer assets optional, and user photos private.
- No new Internet, location or broad storage permission.
- Operational quota stop at 90–92% used; never deliberately exceed 95% in either shared window.
- The user cannot interact with the phone until tomorrow. Record unavailable physical checks and continue locally.

## Review Focus

- Process recreation and stale asynchronous image results: current selections must win.
- Invalid ZIPs and failed writes: previous library and settings must remain usable.
- Large text and narrow/landscape layouts: controls must remain reachable.
- Undo/cancel/save: drafts must not leak into persisted settings.
- Missing widget providers or local library: retain recoverable preferences and show graceful fallback.

---

### Task 1: Close beta21 baseline and create the execution ledger

**Files:** `docs/QA-beta21.md`, `docs/perf-audit-2026-10-22-app.md`, `README.md`, `BETA.md`; private evidence in workspace `work/qa-beta21`.

**Interfaces:** Consumes the installed beta21 and existing backup/QA scripts. Produces the documented baseline and task ledger for later comparisons.

- [x] Verify existing unit suite and record final optimized idle/scroll sample under available device conditions (samples inconclusive).
- [x] Exercise library rejection/persistence, normal icons, current avatar/weather and dashboard controls as availability permits (remaining recovery checks carried into Task 2).
- [x] Document only observed outcomes, remaining checks and current build commands.
- [x] Commit baseline documentation after verifying it matches evidence.

### Task 2: Improve resource and settings integrity

**Files:** `data/LocalImages.kt`, `data/RetroAssets.kt`, `LauncherViewModel.kt`, `widgets/WidgetController.kt`, focused new unit tests where pure logic can be extracted.

**Interfaces:** Retain existing `loadLocalImage`, theme apply and import APIs unless tests demonstrate a necessary change.

- [ ] Reproduce highest-impact cancellation, stale-result, archive or lifecycle defect with a focused failing test.
- [ ] Apply the smallest fix and verify RED→GREEN, then run the whole unit suite.
- [ ] Validate malformed library recovery and process recreation on the available target; record unavailable cases.
- [ ] Commit each coherent fix with its verification result in the ledger.

### Task 3: Add visual dashboard editing and undo

**Files:** `data/DashboardSettings.kt`, new `data/DashboardDraft.kt`, `ui/DashboardEditor.kt`, new focused `DashboardDraftTest.kt`.

**Interfaces:** `DashboardDraft` owns immutable `DashboardSettings` history and exposes update/undo/reset operations. `DashboardEditorDialog` persists only through the existing `setDashboard(DashboardSettings)` call.

- [ ] Write tests for sequential edits, undo boundaries, reset followed by undo, independent zone ordering and unchanged base state when a draft is discarded; run to observe missing behavior.
- [ ] Implement pure draft/history logic, bounded settings and zone-aware moves; run the suite.
- [ ] Add a schematic themed preview with selectable blocks, explicit save/cancel and accessible undo. Reuse the four existing anchors and sizes.
- [ ] Verify large text/landscape and save/cancel/persistence where available; commit with evidence.

### Task 4: Add Motorola RAZR appearance and polish shared surfaces

**Files:** `data/LauncherTheme.kt`, `ui/theme/AuraTheme.kt`, `ui/theme/RetroDesign.kt`, `ui/theme/SkinPattern.kt`, theme editor and focused theme persistence tests.

**Interfaces:** Add one `LauncherTheme` entry that round-trips through `fromStoredValue`, backup allowlists and theme controls. Default remains `NOTHING`; existing theme names continue to reload.

- [ ] Research primary or historical device references and record provenance separately from artwork.
- [ ] Write a failing behavior test for storing/restoring the new theme and fallback compatibility.
- [ ] Implement the theme with original code/vector artwork and coherent clock, surfaces, accents and icon fallbacks. Preserve layout and normal icon mode.
- [ ] Verify theme switching, contrast, reduced motion, selection persistence and backup compatibility; commit.

### Task 5: Verify performance and deliver

**Files:** performance/QA docs, `docs/BETA22.md`, README/BETA guide, packaging scripts, final APK/source/checksums.

**Interfaces:** Release-derived beta with R8 and compiler reports; no arbitrary dependency upgrades or speculative stability annotations.

- [ ] Rank measured hotspots and apply only justified focused fixes, reading the relevant Compose skill first.
- [ ] Run unit tests, optimized build, lint, signing verification and available runtime checks.
- [ ] Dispatch one independent final reviewer using the requesting-code-review skill; address important findings with targeted regression tests.
- [ ] Package source without builds, private evidence, user assets or keys; write checksums and evidence-backed report.
- [ ] Check quota, save the ledger, stop heartbeat when complete and mark the goal complete only when required work is actually finished.
