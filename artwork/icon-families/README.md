# Aura icon families

192 original vector variants:32 functional drawings across Nothing/CMF, Sense, TouchWiz Nature, LG Optimus, Xperia Flow and RAZR V3. These are new interpretations, not manufacturer artwork or192 package-specific app logos.

## Source of truth

`tools/generate-icon-families.py` deterministically writes SVG masters here, VectorDrawable resources under `app/src/main/res/drawable/aura_*.xml`, `manifest.json` and the compile-time Kotlin `FamilyIcon` resource catalog. Run it from any working directory with Python3; no third-party packages required. Do not edit generated mappings/resources independently.

Gradients and objects are native vector paths. Nothing uses nine-cell symbolic matrices of circular points; RAZR uses LCD sprites and a metallic bezel. The other families have object-specific geometry and materials: HTC physical objects, Samsung floating Nature objects, LG warm porcelain layers, Sony thin luminous glass. Construction primitives can be shared; files are variants of32 functions, not192 unrelated illustrations.

`contact-sheet.svg` compares the eight core functions across all six themes. SVG masters can be rendered by any standards-compliant SVG renderer. The native gallery uses the compiled Android resources and offers32/42/60dp plus light/dark backgrounds.

## Identity and provenance

All artwork in this directory is authored for Aura and covered by the repository's MIT license. No original manufacturer icon, logo, font or personal reference image is bundled. Historical lineage and limits: `docs/HISTORICAL-THEMES.md`.

The runtime exact-package catalog is separate in `FamilyCatalog.kt`. It maps known system functions only. Modern unmatched apps retain their own drawable. Personal replacements, explicitly selected Android icon packs and enabled personal collections keep priority. The older substring category lookup is retained solely for optional personal collection compatibility, not to assign these default native drawings.

Coverage must be reported separately as32 functions,192 family variants and the actual matching installed packages. Some functions in the gallery are reserve artwork without a system-package mapping. A phone with only modern third-party apps will use more native fallbacks; no claim of universal package coverage is made.
