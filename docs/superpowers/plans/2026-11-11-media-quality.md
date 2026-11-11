# Beta73 media and adaptive quality

User approved proposals 2 and 1 on 11 November 2026. Execute inline using executing-plans/ponytail/animate/accessible-animation. AGENTS.md forbids automatic suites: build/lint/source review only. Keep a 10% quota reserve, stop at 90%.

- [x] Extend existing media session metadata with real position, duration, speed/time and seek/play capabilities; retain the current paused session only if no other audio is playing. Validate commands against current session/action/state and bound seeks to known duration.
- [x] Expand PS4 artwork into a media sheet using existing panel progress. Keep artwork origin during opening/closing; show real timing, read-only progress if seeking unavailable, no invented duration. Update at 500ms only while sheet visible/resumed/playing. Preserve generic audio behavior and session isolation.
- [x] Add a lifecycle-owned Window FrameMetrics quality observer: bounded 60-frame windows, reduce decorations after >=6 slow frames, recover after three windows <=1 slow frame. Ignore first draw and invalid metrics, use actual deadline when available. No FPS cap, input changes, extra idle clock or persistent preference writes. Reduce ribbon glow/motes, media curves and ornamental panel detail only.
- [x] Compile/lint, inspect source, record version 0.51.0-beta73/code73 with limitations in CHANGELOG/BETA73/MASTER/CONTINUITY/README, push branch. Installation/real playback/performance only if verified.

Scope: AudioPlaybackMonitor, LauncherViewModel, HomeScreen, ThemedAudioWidget, Ps4MediaWidget, new MediaDetails and DecorationQuality, cached decorative draw consumers. Existing permission consent, personal icons, layouts and sound choices unchanged. Quality is a heuristic from Activity metrics, not proof of compositor FPS. Previous Static comparison remains unqualified. Fresh local review only if useful within quota.

Final build/lint passed in 57s, APK version/signature/hash verified locally; real-device media and quality acceptance pending. No independent cloud review added at 87% quota.
