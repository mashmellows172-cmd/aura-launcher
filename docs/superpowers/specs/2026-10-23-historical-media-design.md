# Aura beta25 — historical identities and active media

## Intent and authority

The user requested an autonomous session of about six hours on 2026-10-23, without routine clarification or permission questions. Refine every historical theme, give each its own animations including weather, and show a themed media widget only during detected playback. The user additionally requested installing the result on the Motorola when finished. Earlier requirements for themed/normal icons, AMOLED Nothing, unchanged functional arrangement, motivational headers and personal asset imports remain applicable.

## Starting state

HEAD f2659aa, branch codex/aura-polish. Beta24 installed. New uncommitted work includes AudioPlaybackMonitor, ThemedAudioWidget and MotivationalCatalog plus integration and retro refinements. These files were copied into a private work/aura-session-* checkpoint before editing. Preserve this contribution and integrate it, rather than resetting to HEAD. Native rotation/large-text verification from beta23 remains incomplete. No performance or visual claims are inherited as proven.

## Approach

Use native Compose, centralized historical profiles and existing theme primitives. Keep the original art and optional manufacturer library separate. Prefer callback-driven Android MediaSessionManager monitoring with an optional NotificationListenerService component; do not read notification contents. A visible-only low-frequency AudioManager fallback provides generic playback indication without metadata or controls. Alternatives rejected: arbitrary session lookup with null listener (requires privileged access), perpetual polling with full metadata reloads, React/WebView components, microphone capture or audio analysis. Magic UI is reference material only.

## Visual contracts

Keep placement of search, alphabetical rail, favorites and app list. Shape, material, typography, clock treatment, artwork and motion differ. Existing custom accent, shape and icon preferences win; high contrast wins over decorative materials.

| Profile | Reference family | Material and clock | Distinct motion |
|---|---|---|---|
| Nothing/CMF | Nothing visual language | Pure black, luminous dot matrix, restrained signal accent | Dot formation and soft LED pulse |
| Sense | HTC Sense clock heritage; personal One M7 archive is labelled separately | Graphite frame, split flip leaves, dimensional subdued chrome | Hinged clock change and soft cloud depth |
| TouchWiz | Galaxy S III Nature UX, 2012 | Water glass, cyan/green, organic rounded highlights | Ripple expansion and rain on glass |
| Optimus | LG G2 era, 2013 | Tactile memo/paper and warm highlight, distinct grouped icon plates | Layered reveal, mosaic/light particles |
| Xperia | Xperia Z1/Flow, 2013 | Thin luminous glass, light type, narrow edges | Long flowing light ribbons |
| RAZR | Motorola RAZR V3, 2004 | Silver/graphite trim, blue LCD, crisp segment art | LCD scan and discrete segment illumination |

These are original reinterpretations within Aura's layout, not assertions of pixel-identical historical software. User-supplied archive model/year is shown accurately, including differences between legacy Sense and the M7 collection. Do not bundle personal assets, sounds or licensed fonts in public sources/APK.

## Media contract

ActiveAudioInfo carries playing state, limited-access state, title/artist/app, available controls and bounded optional artwork. Choose an actually playing session, preserve the current session when equally eligible, and subscribe to all candidates so a paused candidate becoming active is observed. Clear stale metadata and subscriptions on revocation/session destruction. Start only while home is visible and media widget enabled; stop releases callbacks/jobs. No microphone, recording, network or foreground service.

With enabled notification access, use that exact component in getActiveSessions and addOnActiveSessionsChangedListener. The service does not inspect StatusBarNotification payloads. Explain the Android access scope before opening the system consent screen; never write secure settings or silently grant permission. Without access, generic isMusicActive fallback has no fabricated app identity. Controls target the displayed session only and are disabled when unsupported. Do not start playback merely to show the widget. Hide after playback stops; preserve pointer/accessibility behavior of unrelated widgets.

## Header contract

Use stable, short Spanish Latin American phrases by theme/day/time bucket. Reuse the existing minute/time/date updates, avoid new frequent timers and live announcements. Theme-specific captions and messages must not replace functional names. Allow disabling phrases. Clock parsing accepts locale formats, single-digit hour and large font scale; only the visible clock surface opens alarms, free space remains available for lock gesture.

## Motion contract

One lifecycle-bound draw-phase clock per scene; no full HomeScreen recomposition per frame. Distinguish weather condition from material. Unknown provider descriptions stay neutral; manual condition selection is decoration, not a forecast. Smooth loop endpoints; no empty frame, abrupt brightness flash or old-provider art while waiting for new data. Honor Android animator settings, reduced motion and high contrast; stop drawing animation off screen. Repetition remains explicit opt-in, no measured battery/FPS claims without measurement.

## Verification and constraints

Compile optimized beta and lint, inspect source independently, check signature, install with adb install -r, and leave APK in Downloads. No uninstall, no global permission changes, no publication. Do not add or run automated tests absent a user request, per current environment instructions; do not carry forward beta23's 30 tests as beta25 evidence. Device observation is conditional on an unlocked available phone; preserve timeout/font/rotation and recent preferences before any reversible inspection. Current connection may expire; continue independent work if unavailable. Maintain last-known-good APK and a private checkpoint. Respect account quota below 95%, checkpoint around 90–92% if necessary. Record actual elapsed work rather than claiming six hours have passed.
