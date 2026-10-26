# PS4 Dynamic Menu implementation plan

Goal: replace the generic launcher body for PS4 with a coherent console-style interface while retaining all real Android apps and user preferences.

Architecture: Ps4Dashboard owns function selection, content ribbon, library and quick menu. HomeScreen supplies existing callbacks and dashboard/media slots; other themes keep their current UI. Use stable IDs, native lazy layouts, lifecycle-bound background,350ms ease-in-out emphasis and reduced-motion support.

Reference: Sony home/function/quick-menu images and public icon assets. Current manual describes13.50+, so it is structural evidence rather than a certificate of firmware9.x pixel parity. Preserve resource origins and never call a recreation an official kit. Noto Sans is the free font fallback; no unlicensed SST bundle.

- [x] Collect official icons, attribution and Noto Sans.
- [x] Add dedicated PS4 header/profile, function strip, content cards, title/action panel, library and quick menu.
- [x] Route controls to real launcher actions or PlayStation App with explicit availability. Keep widgets/media accessible and custom icons intact.
- [x] Source and compile/lint verified; no automatic suites.
- [ ] Native visual review pending device unlock; do not claim firmware9.x/HD parity.
- [x] Beta31 signed, installed and documented; source checkpoint prepared.

Accepted design choices: horizontal two-tier console structure adapted to portrait without forcing landscape; touch selects a card before opening it via Iniciar or a second tap. Native keyboard/D-pad focus remains operable. Static scene for reduced motion/high contrast. No PSN data or invented trophy counts.

Skill ps4-theme-fidelity installed from the user-provided local folder. Treat its historical claims as design suggestions requiring source verification; direct user timing350ms and observed Sony toolbox glyph override conflicting suggestions.
