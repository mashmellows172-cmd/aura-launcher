# H5 validation by primary agent

F1 accepted: holder geometry differed by rendering path (beveled native was rounded, artwork cut; squircle/rounded radii differed). Shared animatedIconHolderShape now supplies both paths, preserving explicit custom-holder gating and Nothing default no-plate behavior. Reduced motion/system scale disables holder interpolation.

F2 rejected: combinedClickable is focusable through AbstractClickableNode/FocusableNode. Adding a second focusable would create redundant targets. Official AndroidX source: https://raw.githubusercontent.com/androidx/androidx/androidx-main/compose/foundation/foundation/src/commonMain/kotlin/androidx/compose/foundation/Clickable.kt . Native keyboard focus behavior remains unverified. The raw report also confuses AppRow with card focus: HistoricalPanel independently uses Surface interaction source.

F3 accepted as low-impact key refinement: monoInk only included when monochrome affects the bitmap; source/pack/revision keys retained. No claim of measured cache hit gain. No theme-in-native-cache change.

Review performed by source inspection, not physical UI/FPS tests. No automated suites or extra permissions.
