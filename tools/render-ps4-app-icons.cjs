const fs=require('fs'),path=require('path');
let sharp;try{sharp=require('sharp')}catch(_){sharp=require(process.env.AURA_SHARP_PATH||'C:/Users/LOQ/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp')}
const root=path.resolve(__dirname,'..'),masters=path.join(root,'artwork/ps4/app-icons');
const catalog=JSON.parse(fs.readFileSync(path.join(masters,'manifest.json'),'utf8'));
(async()=>{for(const e of catalog.entries){await sharp(path.join(masters,e.key+'.svg'),{density:216}).resize(192,192).webp({lossless:true,effort:4}).toFile(path.join(root,'app/src/main/res/drawable-nodpi',e.resource+'.webp'))}console.log('Rendered '+catalog.count+' PS4 resources at192px')})().catch(e=>{console.error(e.message);process.exitCode=1});
