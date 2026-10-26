# OpenCode review dispatch — beta38

User authorized sharing only necessary private source with OpenCode/model provider. Three isolated, tools-denied review sessions were dispatched using opencode/nemotron-3.5-super-vl-free. Each context includes selected numbered source excerpts only; no credentials, keys, backups or device evidence.

All three returned provider.free-limit / HTTP429. No model findings, edits or successful review. No retries, provider billing change or paid subscription. Local preparation/results: work/opencode-beta38-review outside repository. Resume only when provider availability changes; do not describe these tasks as completed.

## Tasks / acceptance

1. Visual: source-backed theme/preview/ergonomic inconsistencies; preserve list/search/A–Z and reject console navigation. Five prioritized proposals, exact citations, explicit uncertainty about historic fidelity. No artwork generation.
2. Variants: persistence/Undo safety audit plus portable backup design; bounded versioned import, copied owned resources, missing-resource behavior and transaction rollback. No implementation of Phase2.
3. Performance: validate benchmark journeys/gates and caching/lifecycle costs. No invented FPS/energy, no automatic phone interaction. Identify false-positive acceptance and source/APK identity gaps.

Required output for every task: severity, exact source line, observed behavior, proposed fix, acceptance criterion, S/M/L effort, uncertainty. Primary reviews source and rejects unsupported conclusions before code changes. Source scope manifest accompanies this report.

## Independent local findings (not OpenCode output)

- Preview parity: ThemeLivePreview.kt:85 always uses railPortrait; HomeScreen.kt:508 selects railLandscape for compact or fontScale>=1.4. Proposal: share sizing logic; accept when preview and Home agree in landscape and large text. Small.
- Recoverable personalization: LauncherViewModel.kt:301 exports FavoritesStore only; Room variants and private wallpapers are absent. Known limitation. Proposal: versioned variant bundle with preview before import; accept round-trip across devices with missing packs reported, base settings untouched. Large.
- Variant gallery: ThemeEditorViewModel.kt:68 creates a fresh UUID per save; ThemeEditorDialog.kt:87 lists names only, with no rename/delete. Proposal: explicit Save as new / Update existing plus thumbnails and safe deletion of unused owned assets. Medium. Current save-as-copy is intentional, not data corruption.
- Stronger performance gate: tools/performance/compare.py compares device metadata/timings, but no commit/APK digest identity or captured thermal/refresh conditions. Documentation requires manual review. Proposal: machine-readable evidence manifest checked before timing comparison; missing identity/conditions must block. Medium. Current CLI pass is explicitly timings-only, not complete promotion approval.
- Preview reachability: ThemeLivePreview.kt:65-69 consumes every pointer event, so long-list inspection cannot scroll. Proposal: a dedicated inspection mode that permits scroll but keeps external actions/widgets inert; acceptance includes reaching bottom apps without launching or locking phone. Medium. Current blocking intentionally protects preview from side effects.

No code/APK changes or tests in this review-only handoff. Beta38 remains installed; Phase2 remains a proposal.
