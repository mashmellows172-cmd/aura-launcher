/** Read public publisher-supplied release metadata, not updates. Private output; no app/account data. */
import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath,pathToFileURL} from 'node:url';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'..');
const work=path.resolve(root,'../../work');
const modulePath=process.env.AURA_GPLAY_MODULE || path.join(work,'play-metadata/node_modules/google-play-scraper/index.js');
const gplay=(await import(pathToFileURL(modulePath))).default;
const snapshot=JSON.parse(fs.readFileSync(path.join(root,'artwork/installed-app-icons/manifest.json'),'utf8')).entries;
const output=path.join(work,'touchwiz-s3/play-release-metadata.json');
const records=fs.existsSync(output)?JSON.parse(fs.readFileSync(output,'utf8')).entries:[];
const seen=new Set(records.map(e=>e.package));
const save=()=>fs.writeFileSync(output,JSON.stringify({method:'Public Google Play released field, en/CL; dates are publisher-supplied and may reflect relisting, not definitive service inception',cutoffYear:2012,entries:records},null,2)+'\n');
for(const app of snapshot){
  if(seen.has(app.package))continue;
  if(app.package.startsWith('org.chromium.webapk.') || app.package==='dev.senda.launcher.debug' || app.package==='com.android.settings'){
    records.push({package:app.package,status:'not-store-app',classification:'unknown'});save();continue;
  }
  try{
    const a=await gplay.app({appId:app.package,lang:'en',country:'cl',requestOptions:{timeout:20000}});
    const raw=a.released || null;
    const time=raw?Date.parse(raw):NaN;
    const year=Number.isFinite(time)?new Date(time).getUTCFullYear():null;
    records.push({package:app.package,title:a.title,publisher:a.developer,released:raw,releaseYear:year,
      classification:year===null?'unknown':year>2012?'post2012-store-listing':'needs-historical-art',
      source:a.url,accessedUtc:new Date().toISOString(),status:'fetched'});
  }catch(error){
    const message=String(error.message).slice(0,240);
    records.push({package:app.package,status:'unavailable',classification:'unknown',error:message});
    if(/429|too many|captcha|forbidden/i.test(message)){save();console.log('Stopped on access/rate restriction; no bypass attempted');break;}
  }
  save();
  if(records.length%12===0)console.log('Recorded '+records.length+'/'+snapshot.length+' app release records');
  await new Promise(resolve=>setTimeout(resolve,1000));
}
save();
console.log(JSON.stringify(records.reduce((counts,e)=>{counts[e.classification]=(counts[e.classification]||0)+1;return counts;},{})));
