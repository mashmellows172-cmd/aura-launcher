"""Render a1x optical contact sheet and count actual SIII reconstruction coverage. No phone access."""
from pathlib import Path
import json
import re
from PIL import Image, ImageDraw, ImageFont

ROOT=Path(__file__).resolve().parents[1]
base=ROOT/'app/src/main/res/drawable-nodpi'
source=(ROOT/'app/src/main/java/dev/senda/launcher/ui/theme/icons/S3Artwork.kt').read_text(encoding='utf-8')
resources=list(dict.fromkeys(re.findall(r'R\.drawable\.(aura_s3_(?:original|period)_[a-z_]+)',source)))
ai=json.loads((ROOT/'artwork/touchwiz-s3/ai/catalog.json').read_text(encoding='utf-8'))['entries']
items=[(r.removeprefix('aura_s3_original_').removeprefix('aura_s3_period_'),base/(r+'.png')) for r in resources]+[(e['key'],base/(e['resource']+'.webp')) for e in ai]
font=ImageFont.load_default(size=12)
out=Image.new('RGB',(860,70+len(items)*76),'#17232e');draw=ImageDraw.Draw(out)
draw.text((12,8),'SIII - period resources and AI interpretations - optical review at1x',font=font,fill='white')
cells=[(32,'#17232e'),(42,'#17232e'),(60,'#17232e'),(32,'#f0ede4'),(42,'#f0ede4'),(60,'#f0ede4')]
for col,(size,_) in enumerate(cells):draw.text((160+col*116,36),str(size)+'px',font=font,fill='white')
for row,(label,file) in enumerate(items):
    y=70+row*76;draw.text((8,y+26),label[:22],font=font,fill='white')
    for col,(size,bg) in enumerate(cells):
        x=150+col*116;draw.rectangle((x,y,x+114,y+74),fill=bg)
        image=Image.open(file).convert('RGBA');image.thumbnail((size,size),Image.Resampling.LANCZOS)
        out.paste(image,(x+(114-image.width)//2,y+(74-image.height)//2),image)
out.save(ROOT/'artwork/touchwiz-s3/review-sheet.png')
snapshot=json.loads((ROOT/'artwork/installed-app-icons/manifest.json').read_text(encoding='utf-8'))['entries']
stock=set(re.findall(r'"([A-Za-z0-9_.]+)"',source.split('fun s3FunctionArtwork')[0]))
ai_packages={e['package'] for e in ai}
covered=[e for e in snapshot if e['package'] in stock|ai_packages]
summary={'inventoryCount':len(snapshot),'covered':len(covered),'ai':len(ai_packages),
    'historicalRoleCounterparts':len(covered)-len(ai_packages),'pending':len(snapshot)-len(covered),
    'meaning':'Prepared coverage only; provenance confidence remains per asset, not OEM-byte certification.'}
(ROOT/'artwork/touchwiz-s3/coverage.json').write_text(json.dumps(summary,indent=2)+'\n',encoding='utf-8',newline='\n')
print(json.dumps(summary))
