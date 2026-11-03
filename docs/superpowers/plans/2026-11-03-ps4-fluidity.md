# PS4 fluidity implementation plan

Goal: improve perceived continuity and legibility in Aura's existing PS4 theme.
Architecture: native Compose, existing frame clock and cached drawing; no new app dependencies.
Design: preserve vertical layout, search, A–Z, one-tap launch, personal icons and stored preferences.
Palette: blue003FA6, depth001D62/000B2B, light008CFF, focus9DEEFF, white text.
Typography: existing Noto Sans; light display, regular reading, medium small controls. Stable app titles during focus transitions.

- [ ] Motion agent: review pacing and draw cost in wallpaper/AuraMotion; implement bounded fixes.
- [ ] Design agent: stable title size and restrained focus; typography hierarchy in existing theme.
- [ ] Integration: shorter, bounded Quick Menu arrival; preserve reduced-motion/system gates.
- [ ] Tooling agent: install requested applicable skills, distinguish web validation from native Android; evaluate local Headroom/OmniRoute use without global routing changes.
- [ ] Supervisor: source review, assembleBeta/lintBeta, signed beta52 with current key; no automatic suites per user instructions.
- [ ] Device: matching limited before/after samples and screenshots; report limits rather than promise FPS.
- [ ] Delivery: CHANGELOG/README/CONTINUITY/BETA52 and delegation evidence; version/hash only after verification.

Evidence before changes: beta51 accumulated gfxinfo had3314frames/76.22%janky, median48ms, p9061ms. This mixed sample is not a controlled benchmark; GPU4950ms values are invalid sentinel data. Source shows delay32ms before frame clock and compounded text/row/icon focus scales. These are concrete targets, not proof of total causality.
