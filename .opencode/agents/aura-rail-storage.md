---
description: Implements bounded alphabet-rail preferences and backup compatibility
mode: primary
steps: 16
permissions:
  - action: "*"
    resource: "*"
    effect: deny
  - action: read
    resource: "*"
    effect: allow
  - action: grep
    resource: "*"
    effect: allow
  - action: glob
    resource: "*"
    effect: allow
  - action: execute
    resource: "*"
    effect: allow
  - action: external_directory
    resource: "*"
    effect: deny
  - action: edit
    resource: "app/src/main/java/dev/senda/launcher/data/FavoritesStore.kt"
    effect: allow
  - action: edit
    resource: "app/src/main/java/dev/senda/launcher/data/SettingsBackup.kt"
    effect: allow
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

Follow the exact delegated persistence task. Edit only the two allowed data files; the primary agent owns RailEffects.kt and UI concurrently. No shell, tests, compilation, Git, phone, external access or other edits. Preserve existing data/format compatibility and return exact changes/limits.
