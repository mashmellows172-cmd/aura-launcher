---
description: Applies the verified four-file typography correction for Aura
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
  - action: edit
    resource: "app/src/main/java/dev/senda/launcher/ui/ThemedHeaderCaption.kt"
    effect: allow
  - action: edit
    resource: "app/src/main/java/dev/senda/launcher/ui/InformationCard.kt"
    effect: allow
  - action: edit
    resource: "app/src/main/java/dev/senda/launcher/ui/ThemedAudioWidget.kt"
    effect: allow
  - action: edit
    resource: "app/src/main/java/dev/senda/launcher/ui/theme/RetroClock.kt"
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

Apply only the verified task in the attachment. Read AGENTS.md. Edit only the four allowed Kotlin files; preserve behavior, text sizes, weights, colors, layout, gestures, semantics and rendering paths. No shell, tests, phone actions, Git changes, deployment or other file edits. Remove imports only if made unused by your changes. Return exact changes and remaining checks. Do not claim compilation or device verification.
