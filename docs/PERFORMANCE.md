# Performance gate — Phase 1

Status: **BLOCKED: no physical reference/candidate measurements yet**. Build success does not approve effects for main. Work remains on codex/phase1-theme-editor. Repository branch protection is not configured by this document; maintainer must require this evidence before merging.

## Isolated packages and journeys

- Daily launcher: dev.senda.launcher.debug. Never clear/uninstall its data for a benchmark.
- Measurement: dev.senda.launcher.benchmark; release optimizations, shell-profileable, original local development signature. Do not make it the default HOME.
- :macrobenchmark LauncherBenchmarks: first coldStartupUncompiled (10 cold launches, TTID and TTFD); then coldStartupProfileRequired (profile mandatory); alphabetAndScroll (FrameTimingMetric, 10 iterations).
- ReportDrawnWhen means app inventory/preferences and icon-pack mapping are ready. It does not claim every bitmap has decoded.
- :baselineprofile BaselineProfileGenerator traverses HOME, A–Z and list. Generation and timing are separate operations. No fabricated handwritten profile is shipped.

## Commands (physical phone free and unlocked)

Build only: `./gradlew :app:assemblePerformance :macrobenchmark:assembleBenchmark :baselineprofile:assemble`.
Generate with `./gradlew :app:generatePerformanceBaselineProfile` (verify it exists with `:app:tasks --all`). Generate only for the isolated performance variant; generator rejects a daily package. Copy a verified generated profile to main only after reviewing its rules; rebuild and check APK assets/dexopt/baseline.prof before the Require measurement.

First timing run: `./gradlew :macrobenchmark:connectedBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=dev.senda.launcher.benchmark.LauncherBenchmarks#coldStartupUncompiled`.
Run the full LauncherBenchmarks class only after the generated profile is packaged. Keep AndroidX JSON, Perfetto traces and device evidence outside git.

## Fair comparison / promotion checklist

Compare base 0714c7c plus identical measurement hooks against candidate, same physical Motorola, OS, refresh mode, temperature, brightness, app inventory, theme, A–Z settings, battery range, compilation mode and connectivity. Record exact source commit and APK SHA256/signing fingerprint. Repeat baseline/candidate alternately; discard throttled or interrupted sessions, not inconvenient results. Benchmark the changed effects explicitly, not just default Nothing.

Run `python tools/performance/compare.py reference.json candidate.json`. Initial timing budget: no regression above 10%, allowing 20ms startup / 1ms frame noise floors. These are provisional engineering limits, not measured device capabilities. Inspect distributions/traces and repeat near-threshold results. A pass here only approves timings, never battery or visual quality.

## Idle and battery (Motorola)

PowerMetric energy support is device-dependent; do not claim Motorola energy from unsupported counters. Use idle-snapshot.py before and after a controlled 15-minute foreground idle period, and a separate screen-off/background period. Start with same theme/state and conditions as reference; no charging, no media, no user interaction. The operator controls screen state; scripts never alter timeout, lock, permissions or battery statistics. Record actual elapsed time. Compare UID CPU/wakelock data and frame activity; no continuous frames for static/reduced-motion themes. Charge counter delta is whole-device and needs repeated paired sessions; do not attribute it to Aura alone. Use an external power meter if the phone cannot provide useful energy evidence. Missing evidence keeps battery acceptance pending.

Required for main: source/APK/profile identity; paired cold/scroll results within budget; reviewed idle traces without new background loops/wakelocks; repeated energy evidence without unexplained regression; manual editor/undo/rotation/theme checks. No device performance claims from compilation or emulator runs.

References: https://developer.android.com/topic/performance/baselineprofiles/configure-baselineprofiles ; https://developer.android.com/jetpack/androidx/releases/benchmark ; https://developer.android.com/topic/performance/benchmarking/macrobenchmark-metrics
