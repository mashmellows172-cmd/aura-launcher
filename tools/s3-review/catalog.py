"""Build the review queue, never generate or activate icon art."""
import hashlib,json,re
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
def main():
 inventory=json.loads((ROOT/'artwork/installed-app-icons/manifest.json').read_text(encoding='utf-8'))['entries']
 mapping={}
 for filename in ['S3Artwork.kt','S3AiArtwork.kt']:
  text=(ROOT/'app/src/main/java/dev/senda/launcher/ui/theme/icons'/filename).read_text(encoding='utf-8')
  for line in text.splitlines():
   hit=re.search(r'R\.drawable\.(aura_s3_\w+)',line)
   if hit:
    for package in re.findall(r'"([a-zA-Z0-9_.]+)"',line):mapping[package]=hit[1]
 original=json.loads((ROOT/'artwork/touchwiz-s3/historical-manifest.json').read_text())
 period=json.loads((ROOT/'artwork/touchwiz-s3/period-pack-manifest.json').read_text())
 evidence={r['resource'].removesuffix('.png'):dict(r,reference='artwork/touchwiz-s3/historical-manifest.json') for r in original}
 evidence.update({r['resource']:dict(r,reference=period['provenanceArticle']) for r in period['assets']})
 entries=[]
 for app in inventory:
  resource=mapping.get(app['package']);info=evidence.get(resource,{})
  files=list((ROOT/'app/src/main/res/drawable-nodpi').glob(str(resource)+'.*')) if resource else []
  if resource and not files:files=list((ROOT/'app/src/main/res/drawable').glob(resource+'.*'))
  if resource:assert files,resource
  entries.append(dict(package=app['package'],label=app['label'],resource=resource,status='EXISTING_CANDIDATE' if resource else 'PENDING',kind='AI' if resource and '_ai_' in resource else 'HISTORICAL_ROLE' if resource else 'UNASSIGNED',reference=info.get('reference','artwork/touchwiz-s3/ai' if resource else ''),confidence=info.get('confidence','AI period interpretation; not original Samsung' if resource else 'Requires verified references and manual optical review'),sha256=hashlib.sha256(files[0].read_bytes()).hexdigest() if files else None))
 assert len(entries)==176 and sum(e['resource'] is not None for e in entries)==20
 output=ROOT/'app/src/main/assets/s3-review.json';output.parent.mkdir(parents=True,exist_ok=True)
 output.write_text(json.dumps(dict(schema=1,total=len(entries),entries=entries),ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
 print('Review queue:20 existing candidates,156 pending. No artwork activated.')
if __name__=='__main__':main()
