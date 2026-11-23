# PS4 Aesthetic Chrome Design

## Intent

Apply the four user-approved visual changes to Aura's PS4 theme: a compact Sony-like HUD, layered PS4 waves, a glass Quick Center, and one shared focus treatment. The experience must remain Aura: vertical apps, search, A–Z, widgets, one-tap launch and personal settings remain unchanged.

## Goals

- Make the upper chrome read as a cohesive PS4 Dynamic Menu surface without copying Sony-owned assets.
- Make background depth react only to meaningful UI state; it must not hold a perpetual animation loop in idle.
- Make the Quick Center feel like a lighter glass control surface which uses the same palette tokens as the selected app row.
- Make selected list rows and the Quick Center originate from one focus language: rim, soft halo and short reflected sweep.

## Non-goals

- No horizontal console carousel, app behavior, new permission, sound, wallpaper download, or system setting.
- No artificial FPS claims, timer-driven ambient animation, raw artwork, or additional external dependency.

## Direction

- **HUD:** avatar/Aura identity, brief existing caption and time retain their current layout but receive a restrained PS4 hierarchy: thin HUD ink, light text and palette-bound separator glow.
- **Depth:** cached blue waves remain the base. A second cached depth field and an interaction-only wave response carry scroll/focus context. Idle geometry stays static.
- **Quick Center:** translucent glass uses palette-specific surface alpha, rim and reflected upper edge. Midnight remains opaque enough for legibility; Vita retains cyan identity.
- **Focus:** app and panel selection share rim color, halo geometry and a finite 200–240 ms reflected sweep. High contrast, power saver, reduce motion, preview and disabled system animator suppress decorative motion.

## Constraints

- Follow `AGENTS.md`: preserve the vertical Aura structure and do not run test suites.
- Validate through `assembleBeta lintBeta`, source/diff review and documentation.
- Preserve user icon packs, normal icons, replacements, widgets and stored preferences.

