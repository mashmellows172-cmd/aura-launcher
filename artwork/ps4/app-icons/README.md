# Aura PS4 app icon catalog

176 identities from the verified Motorola snapshot. Original blue glass square plates, ice-blue/white identities and a thin console-style edge. These are Aura interpretations of Android apps, not an official Sony Android icon pack. Most apps have no PS4 equivalent.

Six functional counterparts use Sony public guide glyphs (settings/messages/contacts/calendar/files/store). Other apps prefer a valid app-provided monochrome layer; solid/empty masks are rejected and use the captured identity instead. Unknown new apps fall back to their normal drawable; no app identity is guessed from package-name keywords.

Rebuild: python tools/generate-ps4-app-icons.py --archive PATH_TO_PRIVATE_CAPTURE.zip, then node tools/render-ps4-app-icons.cjs. Input ZIP stays private; SVG masters and production192px losslessWebP are versioned. No tracing, SVG parsing or image effects execute while scrolling. tools/review-theme-assets.py produces the optical review sheet from actual resources.

Thematic AUTO uses the catalog. Normal icons, installed icon packs, personal replacements and imported collection priority are preserved. Switching theme does not delete any choice. Source guide metadata is in ../official-assets.json; brand identities remain third-party marks.
