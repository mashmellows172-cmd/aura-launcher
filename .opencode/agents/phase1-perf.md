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
    resource: "macrobenchmark/**"
    effect: allow
  - action: edit
    resource: "baselineprofile/**"
    effect: allow
  - action: edit
    resource: "tools/performance/**"
    effect: allow
  - action: edit
    resource: "docs/performance/**"
    effect: allow
  - action: read
    resource: "beta/*"
    effect: deny
  - action: read
    resource: ".git/*"
    effect: deny
---
Only edit allowed files. No shell/build/tests/Git/network/phone/subagents. Read actual source before work; report changes and integration instructions. Never claim execution/validation not performed.
