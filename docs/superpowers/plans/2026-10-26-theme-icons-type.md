# PS4 icon identity and per-theme typography

Goal: retain Aura navigation, add a PS4-style app icon for each of176 captured identities, and apply a distinct bundled family to every theme across all Material typography roles.

Icon design: square blue glass plates with subtle edge, white/ice-blue flat identities, no circular badge. Prefer the app-provided monochrome layer; otherwise derive inspectable SVG identity paths from the captured public launcher artwork. Pre-render lossless192px WebP at build time, never trace during scrolling. Label as Aura interpretations, not official Sony app icons (most Android apps have no console equivalent). Preserve ORIGINAL, custom overrides and installed pack priority. Unknown newly installed apps retain native artwork. No PSN or navigation changes.

Fonts: Nothing existing Geist/Geist Mono; Sense period Droid Sans; SIII period Roboto; LG free Lato analogue; Xperia period Roboto Condensed analogue; RAZR free Share Tech Mono analogue; PS4 existing Noto Sans SST analogue. Obtain redistributable sources and licenses, record hashes and exact provenance; do not claim proprietary font equivalence or manufacturer verification. All15 typography roles must use each family's fonts, with legible body sizing retained.

Steps: source fonts; author reproducible PS4 catalog and optical sheet; integrate lookup and theme typography; source review and compile/lint only; sign beta33, install if authorized Motorola available; update CHANGELOG and source checkpoint. No automated suites per user instruction.
