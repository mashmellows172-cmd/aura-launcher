# Per-theme typefaces

All15 Material typography roles now resolve to the theme's bundled family (including app rows, menus, search, headings and dialogs), rather than only modifying a few headings. User font size and Android accessibility scaling remain active. Nothing keeps its existing Geist/Geist Mono combination.

| Theme | Bundled family | Historical status |
| --- | --- | --- |
| Nothing/CMF | Geist + Geist Mono | Free industrial analogue; not Nothing Ndot/Lettera |
| HTC Sense | Droid Sans, Android2.3.7 | Authentic period AOSP font; exact HTC firmware use not certified |
| Galaxy SIII | Roboto, Android4.0.4 | Authentic ICS-era AOSP files; no claim that every Samsung text role used this family |
| LG Optimus | Lato | Free humanist analogue; not proprietary LG Smart Gothic |
| Xperia | Roboto Condensed, Android4.3 | Period free Android family; not licensed Sony SoMA/SST |
| Motorola RAZR | Share Tech Mono | Free LCD/terminal analogue; not extracted Motorola firmware font |
| PS4 | Noto Sans Light/Regular/Medium/SemiBold/Bold | Free alternative to Sony SST, as approved in the user's request |

Original URLs/hashes: manifest.json; Noto and Geist provenance/licenses remain in artwork/ps4 and font license documentation already in this repository. Droid/Roboto include AOSP notices and Apache2.0; Lato and Share Tech Mono include OFL licenses. Fonts were obtained from AOSP and Google Fonts repositories, never from proprietary firmware.

Primary references: https://opensource.googleblog.com/2015/05/roboto-googles-signature-font-is-now.html (ICS fonts released under Apache); https://www.sony.com/en/SonyInfo/design/stories/sst-font/ (SST design and use across Sony devices/PS4). A different family by itself is not proof of100% manufacturer fidelity. preview.png is an optical font-file review, not a device screenshot.

PS4 Medium/SemiBold/Bold added26 October2026 from the same upstream repository under the existing OFL license; hashes in manifest.json. Actual weights, not synthetic bold or Sony SST.
