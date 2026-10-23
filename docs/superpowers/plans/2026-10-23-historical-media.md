# Historical themes and active media Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans inline. The user's explicit autonomous authorization supersedes approval pauses. One independent source review at the end.

**Goal:** Deliver a differentiated, historically referenced Aura beta with reliable active-media appearance and theme-specific motion.
**Architecture:** Historical profile tokens feed existing clock/cards/editor and new media card. Android callbacks provide media state while Home is visible, optional generic fallback without special access. Existing stores preserve user settings.
**Tech Stack:** Existing Kotlin/Compose, Android framework media APIs, API29–37, JDK17, existing Gradle toolchain.
**Spec:** docs/superpowers/specs/2026-10-23-historical-media-design.md

## Global Constraints

- Preserve uncommitted prior contributions and user data; no uninstall or remote publication.
- Original/native components; manufacturer resources remain optional personal imports.
- Nothing AMOLED default; functional placement and icon choices remain stable.
- Pause decorative motion for accessibility/off-screen; no fabricated weather or media metadata.
- No new automated tests/suite runs; compilation/lint/source review are the verification gates, runtime limits recorded explicitly.
- Prefer existing feature checkout to avoid losing the prior contribution and toolchain paths. Guard against concurrent edits.

## Review Focus

- Permission absent/revoked while playing: no crash, stale title, wrong controls or listener leak.
- Multiple sessions, session destroyed and audio widget disabled: deterministic visible state and stopped work.
- Reduced motion, high contrast, large text and narrow orientation: usable labels/targets and no animated layout instability.
- Each actual theme has distinct materials and animation signatures; custom finishes still override defaults.
- Device unavailable or user interaction: no forced navigation, stale backup restoration or unsupported completion claim.

### Task 1: Baseline and historical contracts

**Files:** existing contribution checkpoint; ui/theme/HistoricalDesign.kt (new); docs/HISTORICAL-THEMES.md.
**Interfaces:** historicalProfile(theme): HistoricalProfile owns material colors, shape, typography, motion signature and accurate reference labels.
- [ ] Preserve contribution, inspect actual assets and primary historical references, compile baseline to identify existing failures.
- [ ] Define and implement reusable theme contracts; record reference/model/year and asset provenance.
- [ ] Confirm source integration and commit a reviewable unit without claiming native approval.

### Task 2: Playback monitoring and controls

**Files:** data/AudioPlaybackMonitor.kt, MediaAccessService.kt (new), AndroidManifest.xml, LauncherViewModel.kt, HomeTools.kt.
**Interfaces:** ActiveAudioInfo; AudioPlaybackMonitor.start/stop/openAudioApp/pause/previous/next; MediaAccessService.connection StateFlow. Existing Home visibility and header preference own subscription lifetime.
- [ ] Replace invalid unprivileged session lookup with optional-component callbacks, stable playing-session selection and clean revocation.
- [ ] Preserve generic limited fallback only while visible/enabled; never attach a paused app's metadata to unrelated playback.
- [ ] Add clear access status/explanation and system settings action. No automatic secure-setting grant.
- [ ] Compile/lint, inspect lifecycle and permission branches, commit.

### Task 3: Cohesive historical surfaces and media widget

**Files:** ui/ThemedAudioWidget.kt, ui/theme/RetroDesign.kt, ui/InformationCard.kt, ui/ThemeEditorDialog.kt, HomeScreen.kt.
**Interfaces:** profile tokens from Task1; media state/actions from Task2. Shared surfaces used by real Home, preview, media and settings.
- [ ] Differentiate clock, surfaces, borders, type and media visualization using the six visual contracts.
- [ ] Add actual transport controls and bounded artwork when available, meaningful empty/limited states and 48dp targets.
- [ ] Keep normal icons, custom finishes, touch regions and high contrast intact; remove misleading "EN VIVO" and simulated spectrum claims.
- [ ] Compile/lint and commit.

### Task 4: Theme motion, weather and stable headers

**Files:** ui/WeatherSweep.kt, ui/theme/ThemeMotion.kt (new), data/MotivationalCatalog.kt, HomeScreen.kt, affected theme primitives.
**Interfaces:** lifecycle-bound scene phase; historical motion signature; pure phraseFor(theme,date,time) updates from existing clock state.
- [ ] Give each weather treatment a distinctive scene/material; ensure periodic particle endpoints and explicit unknown-condition fallback.
- [ ] Add deliberate theme-specific transitions to clock/card surfaces without animating full app-list layout.
- [ ] Fix time-bucket invalidation and repeated message catalog; support saved opt-out and accessibility.
- [ ] Compile/lint, document evidence and commit.

### Task 5: Review, device delivery and report

**Files:** docs/BETA25.md, docs/QA-beta25.md, beta artifacts, preserved execution ledger.
**Interfaces:** signed update of dev.senda.launcher.debug; existing public Android/Aura flows.
- [ ] Dispatch one independent fresh source reviewer; fix Important/Critical findings, record minor limits.
- [ ] Final optimized build/lint, same signing certificate, source ZIP excluding private data, SHA256 sums.
- [ ] Install on connected Motorola with -r and copy APK to Downloads; verify installed version. If unavailable, preserve package and report delivery pending.
- [ ] Observe available device state without interfering with the user; record runtime/rotation/theme/media permission limits honestly.
- [ ] Preserve ledger, coherent commits and a concise self-contained delivery report.
