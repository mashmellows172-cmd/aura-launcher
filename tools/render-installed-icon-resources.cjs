/** Rasterize authored SVG scenes once at build time; never trace or filter in a scrolling UI. */
const fs = require('fs');
const path = require('path');
let sharp;
try { sharp = require('sharp'); }
catch (_) { sharp = require(process.env.AURA_SHARP_PATH || 'C:/Users/LOQ/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp'); }
const root = path.resolve(__dirname, '..');
const masters = path.join(root, 'artwork/installed-app-icons');
const manifest = JSON.parse(fs.readFileSync(path.join(masters, 'manifest.json'), 'utf8'));
const destination = path.join(root, 'app/src/main/res/drawable-nodpi');
fs.mkdirSync(destination, { recursive: true });
(async () => {
  let count = 0;
  for (const app of manifest.entries) {
    for (let i = 0; i < manifest.themes.length; i++) {
      await sharp(path.join(masters, manifest.themes[i], app.key + '.svg'), { density: 216 })
        .resize(192, 192).webp({ lossless: true, effort: 4 })
        .toFile(path.join(destination, app.resources[i] + '.webp'));
      count++;
    }
  }
  console.log(`Rendered ${count} lossless WebP resources at192px; vector masters retained`);
})().catch(error => { console.error(error.message); process.exitCode = 1; });
