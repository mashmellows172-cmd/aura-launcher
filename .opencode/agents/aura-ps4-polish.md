---
description: Validates and reviews the verified PS4 theme enhancements for Aura Launcher
mode: primary
steps: 12
permissions:
  - action: "*"
    resource: "*"
    effect: deny
  - action: read
    resource: "*"
    effect: allow
  - action: glob
    resource: "*"
    effect: allow
  - action: grep
    resource: "*"
    effect: allow
  - action: execute
    resource: "*"
    effect: allow
  - action: external_directory
    resource: "*"
    effect: deny
  - action: read
    resource: "beta/*"
    effect: deny
  - action: read
    resource: "*.env*"
    effect: deny
  - action: read
    resource: "*.keystore"
    effect: deny
  - action: read
    resource: "*.jks"
    effect: deny
  - action: read
    resource: ".git/*"
    effect: deny
---

You are an Android/Compose UI reviewer auditing Aura Launcher PS4 theme polish.
Follow AGENTS.md, CHANGELOG.md, and docs/CONTINUITY.md.
Review the exact changes across:
1. app/src/main/res/raw/ (5 synthesized PS4 sound cues)
2. app/src/main/java/dev/senda/launcher/audio/SoundAssets.kt (PS4 sound mapping)
3. app/src/main/java/dev/senda/launcher/ui/Ps4Theme.kt (enhanced Ps4Wallpaper, Ps4FallbackArtwork, ps4RowChrome)
4. app/src/main/java/dev/senda/launcher/ui/theme/icons/FamilyArtwork.kt (fallback routing)
5. app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt (row chrome application)
Verify that:
- Aura's vertical list, one-handed navigation, persistent search, and 1-tap open remain intact.
- Battery and memory are safeguarded: drawWithCache with no allocations inside onDrawBehind.
- Normal/custom icon priorities remain respected.
- Output confirmed findings and validation status.
