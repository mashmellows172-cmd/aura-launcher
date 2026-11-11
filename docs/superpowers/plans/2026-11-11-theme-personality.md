# Aura theme personality · implementation plan

> For agentic workers: use executing-plans inline with one bounded final review. AGENTS.md overrides suite suggestions: build/lint and source/visual review only.

Goal: implement the user's selected proposals 1, 2, 6, 4: coherent PS4 materials, cover-driven media atmosphere, system glyph family, and deeper per-theme material/interaction identity.

Architecture: extend HistoricalPanel, ps4Material and existing theme drawing. One Home-owned media color state, sampled off main from current local artwork and consumed only in draw/layers. Original cached vector glyphs shared by system controls; personal app icons and packs unchanged. Finite press/focus transitions reuse existing interaction sources.

Spec: the user's selection on 11 November 2026 approves these four proposals. Preserve vertical list, search, A–Z and one-tap launches. PS4 keeps its current faithful structure. Nothing uses angular silhouettes; Razr machined edges; TouchWiz water/gloss. Other historical themes retain their own material vocabulary. No new sounds are bundled: existing per-theme sound choices are preserved. No new permissions, network calls, large art files or dependencies.

- [ ] Material/glyph task: Ps4Material.kt, new ThemeSystemIcon.kt, HistoricalDesign.kt, ThemedSearchField.kt, HomeScreen.kt, InformationCard.kt, Ps4QuickMenu.kt and existing media controls. Share edge direction and panel treatment. Cache paths/brushes. Match existing semantic actions and disabled states.
- [ ] Media task: new MediaAtmosphere.kt, HomeScreen provider, Ps4LiveWallpaper feedback and Ps4MediaWidget. Sample at most 144 pixels once per artwork change off main; skip hardware/recycled artwork safely. Tint decorative surfaces only, preserve text contrast and personal wallpapers. Finite cover color/presence transitions, resting on pause. Static wallpaper stays static; reduced motion/power/high contrast handled explicitly.
- [ ] Theme identity task: HistoricalPanel press feedback and cached per-theme signatures. Nothing indexed rectangular marks, Razr beveled hinge, TouchWiz lens/water, PS4 glass/light; preserve Sense, Optimus, Xperia vocabulary and defaults. No added idle clocks or per-row timers. Existing sounds remain available per theme; no unsupported claim of newly created official audio.
- [ ] Review: local Ollama aesthetic proposal, source review of APIs/lifecycle/contrast, one fresh-context final review if quota allows, then assembleBeta/lintBeta. No automated suites.
- [ ] Delivery: 0.50.0-beta72/code72, CHANGELOG/BETA72/MASTER/CONTINUITY/README in same delivery commit, publish branch. Verify signing and update over beta71 when connected, record actual hash/version. Do not infer physical animation quality from compilation.

Rulings: use the existing isolated beta58 worktree on a new branch. User selection authorizes implementation without another design approval round. Keep Ambient deferred because the prior Static comparison is unconfirmed. Use local Qwen only for a bounded aesthetic proposal; previously incorrect mathematics/coroutine reviews are not reused. Respect user's 90% cap and preserve 10% reserve.

Validation targets: all four material identities distinguishable in source and sample panels; PS4 blue/white/midnight contrast retained; system controls preserve labels/touch areas; media without artwork/generic audio falls back; no frame polling introduced; live controls and real artwork require a consenting active media session for physical verification.
