# Installed-app icon interpretations

176 verified launcher identities, six prerendered Android WebP variants per identity, with editable SVG masters. This is a device-inventory coverage checkpoint, not a claim of1056 historically official icons or entirely original brand symbols.

The private source archive was rendered from public Android PackageManager icons on the user's authorized Motorola. Original source PNGs, device inventory timestamps/serial and app data are not bundled here. These assets derive recognizable symbols from those app icons, then apply native original material frames, contour quantization, optical sizing, contrasting ink and dot/LCD treatments. Ten system-function identities reuse Aura's previously authored historical objects in the four Android-era families. Short intentional wordmarks replace long unreadable text in selected dot icons.

Code generators remain inspectable. Third-party app marks retain their respective ownership; the project's code license does not claim ownership of those marks. There are no HTC/Samsung/LG/Sony/Motorola firmware extractions or licensed icon-pack downloads.

Review status: contact-sheet inspection caught and corrected empty glyphs from over-aggressive edge removal and white-on-light symbols. This is a first complete inventory coverage pass. Individual historical material fidelity, fine wordmarks and real-device optical quality still need refinement. In particular, generic template frames are a starting material language, not proof that modern app logos existed in classic firmware.

Run tools/generate-installed-app-icons.py with the private archive and inventory to reproduce; requires Pillow. Generated SVG masters and production WebP share exactly the same scene. Runtime has no tracing, bitmap conversion, network or animation loop for these icons. Personal replacements, selected packs/collections and Normal icon mode retain precedence.

Performance ruling: the initial contour XML generated1248 VectorPath warnings. Production now uses lossless192px WebP rendered once from the same SVG scene. Optional --vector-resources export is for inspection; do not ship the verbose contour XML. Android decodes the fixed-size image instead of parsing thousands of path commands while scrolling.
