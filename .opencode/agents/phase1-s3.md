---
description: Bounded Phase1 implementation
mode: primary
steps: 45
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
    resource: "app/src/main/java/dev/senda/launcher/ui/S3ReviewDialog.kt"
    effect: allow
  - action: edit
    resource: "app/src/main/java/dev/senda/launcher/data/S3ReviewCatalog.kt"
    effect: allow
  - action: edit
    resource: "app/src/main/assets/s3-review.json"
    effect: allow
  - action: edit
    resource: "tools/s3-review/**"
    effect: allow
  - action: edit
    resource: "docs/icon-packs/S3-REVIEW-PIPELINE.md"
    effect: allow
  - action: read
    resource: "beta/*"
    effect: deny
  - action: read
    resource: ".git/*"
    effect: deny
---
Only edit allowed files. No shell/build/tests/Git/network/phone/subagents. Read actual source before work; report changes and integration instructions. Never claim execution/validation not performed.
