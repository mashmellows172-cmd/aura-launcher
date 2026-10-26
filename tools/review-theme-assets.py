"""Render review sheets from production icon resources and bundled fonts (not a device screenshot)."""
from PIL import Image,ImageDraw,ImageFont
from pathlib import Path
import json
ROOT=Path(__file__).resolve().parents[1]
meta=json.loads((ROOT/'artwork/ps4/app-icons/manifest.json').read_text(encoding='utf-8'))
font=ImageFont.truetype(str(ROOT/'app/src/main/res/font/noto_sans_regular.ttf'),11)
out=Image.new('RGB',(960,70+((len(meta['entries'])+7)//8)*100),'#0A1933');draw=ImageDraw.Draw(out)
draw.text((16,15),'PS4 - captured identities - production icon review at42px',font=font,fill='white')
for i,e in enumerate(meta['entries']):
 x=(i%8)*120;y=60+(i//8)*100
 im=Image.open(ROOT/'app/src/main/res/drawable-nodpi'/(e['resource']+'.webp')).convert('RGBA');im.thumbnail((42,42),Image.Resampling.LANCZOS)
 out.paste(im,(x+39,y+5),im);draw.text((x+5,y+55),e['label'][:17],font=font,fill='white')
out.save(ROOT/'artwork/ps4/app-icons/preview.png')
rows=[('Nothing','geist_variable.ttf'),('HTC Sense','droid_sans_regular.ttf'),('Galaxy SIII','roboto_ics_regular.ttf'),('LG Optimus','lato_regular.ttf'),('Xperia','roboto_condensed_regular.ttf'),('RAZR','share_tech_mono.ttf'),('PS4','noto_sans_light.ttf')]
im=Image.new('RGB',(1050,630),'#101923');d=ImageDraw.Draw(im)
for i,(name,file) in enumerate(rows):
 y=i*90;d.text((18,y+10),name,font=font,fill='#91BDEC');f=ImageFont.truetype(str(ROOT/'app/src/main/res/font'/file),28)
 d.text((190,y+15),'Miércoles · Cámara · Ajustes · 12:45',font=f,fill='white')
im.save(ROOT/'artwork/theme-fonts/preview.png')
from collections import Counter
print(json.dumps({'covered':meta['count'],'origins':dict(Counter(x['origin'] for x in meta['entries']))}))
