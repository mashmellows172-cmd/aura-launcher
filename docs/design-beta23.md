# Native design system — beta23

Replica design adaptation for Kotlin/Compose: the semantic colors are `MaterialTheme.colorScheme`; skin accent, holder and artwork come from `LocalSkinStyle`; the industrial font/neutral tokens remain in `IndustrialDesign.kt`. CSS/Tailwind are not used by this Android application.

## Information card

- Category in the theme's label typography, 12sp, muted foreground. Value at the selected 12–20sp, 1.3 line height, normal surface foreground. Original 20dp vector glyph, 12dp horizontal/8dp vertical inset, 10dp gap.
- Nothing: black home, neutral raised card, Geist/Geist Mono, restrained signal glyph and outline. CMF accent uses the same existing skin accent option.
- Retro: existing theme radius, surface-to-variant gradient and outline; Sense medium captions, Xperia light spaced captions, RAZR monospace. Imported wallpaper/icon sources are unchanged.
- Minimum 48dp target; configured height is a minimum, enlarged when large text requires it. Category/value remain in one accessible button; ellipsis does not truncate its accessible description.
- States: pressed feedback through Material Surface; empty/error/permission labels remain actionable; preview selection has a stronger accent outline. Widgets use an explicit placeholder in preview and never create a duplicate host.

## Editor

Block selectors, undo and reset appear before details. The sample preview is optional, collapsed initially, and uses the real clock/card primitives and theme. The sample label explicitly distinguishes it from live weather/events. Draft state, selection and preview visibility survive recreation; Save alone commits preferences.

## Responsive rule

`dashboardHalfWidth` drives home and preview: only non-widget blocks with half width selected, available width >=280dp, font scale <=1.5. Other cases use full width. Font scaling may grow the card beyond its selected minimum height. The preview's modal width can differ from the home's available width; it follows the same rule at its own width.

## Contrast

`ThemeContrastTest.actualThemeTextPairsMeetBodyContrast` checks every actual built-in palette in both system modes. Pairs: onBackground/background, onSurface/surface, onSurfaceVariant/surfaceVariant; threshold >=4.5:1. Gradients use those same checked endpoint surfaces and foregrounds. The test checks text roles, not arbitrary imported wallpapers or every composed pixel; native screenshots and high-contrast checks supplement it.

No new animations, fonts, permissions, manufacturer assets or network dependency were added.
