"""Generate six native vector interpretations from the user's captured launcher icon references.

Input archive stays private; no APK extraction, network, runtime tracing or opaque bitmap fallback.
Generated artwork is derived from installed application identities, not claimed as original brand art.
Requires Pillow for build-time contour cells. Android loads prerendered WebP resources; SVG/XML masters remain inspectable.
"""
import argparse
from collections import Counter, deque
import hashlib
import importlib.util
import io
import json
from pathlib import Path
import re
import zipfile
from PIL import Image, ImageOps, ImageChops, ImageStat, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location("families", ROOT / "tools/generate-icon-families.py")
families = importlib.util.module_from_spec(spec); spec.loader.exec_module(families)
Scene = families.Scene
THEMES = families.THEMES
# Explicit historical functional counterparts; modern third-party identities stay app-specific.
# Deliberate compact wordmarks for identities whose long text vanishes at icon size.
DOT_WORDMARKS = {
    "io.instaleap.ecommerce.acuenta":"aC", "com.unoafpapp":"UNO",
    "com.amazon.dee.app":"A", "com.amazon.mp3":"AM", "com.amazon.clouddrive.photos":"AP",
    "com.betterlife.connect":"BL", "com.brother.printservice":"BR", "cl.buk.app":"buk",
    "com.canva.editor":"C", "com.disney.disneyplus":"D+", "com.qubits.fw3.doggis":"DOG",
    "com.falabella.falabellaApp":"F", "com.amazon.storm.lightning.client.aosp":"TV",
    "com.wbd.stream":"MAX", "com.kfcecuador.kfc":"KFC", "com.peqconsultores.lipigas":"LIP",
    "cl.metromuv.app":"MUV", "cl.cencosud.giftcard":"GC", "cl.transbank.onepay":"1P",
    "cl.com.papajohns":"PJ", "com.getjusto.apps.tarragonacl":"TG", "com.suno.android":"SUN",
    "cl.tapp":"Tapp", "cl.wom.transformacion.appwommobile":"WOM", "com.wizconnected.wiz2":"WiZ",
}
COUNTERPARTS = {
    "com.google.android.dialer":"phone", "com.google.android.apps.messaging":"message",
    "com.google.android.contacts":"contacts", "com.google.android.calendar":"calendar",
    "com.google.android.deskclock":"clock", "com.google.android.calculator":"calculator",
    "com.android.settings":"settings", "com.motorola.audiorecorder":"recorder",
    "com.google.android.apps.nbu.files":"files", "hr.dtakac.prognoza":"weather",
}


def foreground(archive, package, full=False):
    name = f"foreground/{package}.png"
    if full or name not in archive.namelist(): name = f"icons/{package}.png"
    image = Image.open(io.BytesIO(archive.read(name))).convert("RGBA")
    # Remove only a uniform edge-connected backing, never an internal logo counter.
    pixels = image.load(); w,h = image.size
    boundary = [(x,y) for x in range(w) for y in (h//10,h-h//10-1)] + [(x,y) for y in range(h) for x in (w//10,w-w//10-1)]
    colors = Counter(tuple(v//24 for v in pixels[x,y][:3]) for x,y in boundary if pixels[x,y][3] > 180)
    if colors:
        dominant,count = colors.most_common(1)[0]
        opaque = sum(pixels[x,y][3] > 180 for x,y in boundary)
        if opaque / max(1,len(boundary)) > .85 and count / max(1,opaque) > .72:
            queue=deque((x,y) for x,y in boundary if pixels[x,y][3]>0 and tuple(v//24 for v in pixels[x,y][:3])==dominant)
            seen=set(queue)
            while queue:
                x,y=queue.popleft(); r,g,b,a=pixels[x,y]
                if a == 0: continue
                if max(abs(c-(q*24+12)) for c,q in zip((r,g,b),dominant)) > 35: continue
                pixels[x,y]=(r,g,b,0)
                for xx,yy in ((x-1,y),(x+1,y),(x,y-1),(x,y+1)):
                    if 0<=xx<w and 0<=yy<h and (xx,yy) not in seen:seen.add((xx,yy));queue.append((xx,yy))
    bounds=image.getchannel('A').point(lambda value: 255 if value > 64 else 0).getbbox()
    if bounds is None or sum(1 for value in image.getchannel('A').getdata() if value > 160) < 100:
        if not full:return foreground(archive,package,True)
        image=Image.open(io.BytesIO(archive.read(f"icons/{package}.png"))).convert("RGBA");bounds=image.getbbox()
    return image.crop(bounds)


def cells(image, side=64, colors=20):
    # Preserve aspect ratio and optical padding; the empty alpha channel stays empty.
    canvas=Image.new("RGBA",(side,side));image=image.copy();image.thumbnail((side,side),Image.Resampling.LANCZOS)
    canvas.alpha_composite(image,((side-image.width)//2,(side-image.height)//2))
    alpha=canvas.getchannel("A")
    rgb=canvas.convert("RGB").quantize(colors=colors,method=Image.Quantize.MEDIANCUT).convert("RGB")
    return [[tuple(rgb.getpixel((x,y))) if alpha.getpixel((x,y))>=96 else None for x in range(side)] for y in range(side)]


def paths(grid):
    # Merge equal-color horizontal runs vertically; one native path per palette color.
    result={};active={}
    for y,row in enumerate(grid+[[]]):
        runs=[];x=0
        while x<len(row):
            color=row[x];end=x+1
            while end<len(row) and row[end]==color:end+=1
            if color is not None:runs.append((x,end,color))
            x=end
        current=set(runs)
        for key,(top,bottom) in list(active.items()):
            if key not in current:
                start,end,color=key
                result.setdefault(color,[]).append((start,top,end-start,bottom-top));del active[key]
        for key in runs:
            active[key]=(active[key][0],y+1) if key in active else (y,y+1)
    return result


def tint(rgb, amount):
    return "#"+"".join(f"{round(c+(255-c)*amount) if amount>=0 else round(c*(1+amount)):02X}" for c in rgb)


def color_art(scene, grid, theme):
    x,y,width,height = (12,11,40,40) if theme=='razr' else (11,10,42,42)
    scale=width/len(grid)
    for index,(rgb,rectangles) in enumerate(paths(grid).items()):
        geometry=" ".join(f"M{x+a*scale:.3f},{y+b*scale:.3f}h{w*scale:.3f}v{h*scale:.3f}h{-w*scale:.3f}Z" for a,b,w,h in rectangles)
        if theme=='razr':paint=tint(tuple(round(v/51)*51 for v in rgb),0)
        elif theme=='optimus':paint=scene.gradient(f"ink{index}",tint(rgb,.10),tint(rgb,-.08))
        elif theme=='sense':paint=scene.gradient(f"ink{index}",tint(rgb,.17),tint(rgb,-.18))
        elif theme=='touchwiz':paint=scene.gradient(f"ink{index}",tint(rgb,.12),tint(rgb,-.08))
        else:paint=scene.gradient(f"ink{index}",tint(rgb,.15),tint(rgb,-.05))
        scene.path(geometry,paint)


def make(theme, glyph, monochrome=None, brand=(30,70,95)):
    s=Scene()
    if theme=='nothing':
        identity = monochrome if monochrome is not None else glyph
        mask = identity.getchannel('A')
        bounds = mask.point(lambda value: 255 if value >= 64 else 0).getbbox()
        if bounds:
            cropped = mask.crop(bounds)
            occupancy = sum(value >= 128 for value in cropped.getdata()) / (cropped.width * cropped.height)
            # Some Android monochrome layers are solid adaptive masks, not logos.
            if monochrome is not None and occupancy > .80:
                identity = glyph
                mask = identity.getchannel('A')
        if ImageStat.Stat(mask).mean[0] > 170:
            luminance = ImageOps.grayscale(identity)
            edge = list(luminance.crop((0,0,luminance.width,1)).getdata())
            if sum(edge) / max(1,len(edge)) > 220:
                luminance = ImageOps.invert(luminance)
            visible = [luminance.getpixel((x,y)) for y in range(identity.height) for x in range(identity.width) if identity.getpixel((x,y))[3] > 160]
            threshold = (min(visible) + max(visible)) / 2 if visible else 128
            mask = ImageChops.multiply(mask, luminance.point(lambda value: 255 if value >= threshold else 0))
        bounds = mask.point(lambda value: 255 if value >= 48 else 0).getbbox()
        if bounds:
            mask = mask.crop(bounds)
        # Sample coverage, rather than hard luminance, so thin strokes survive.
        mask.thumbnail((23,23), Image.Resampling.LANCZOS)
        pitch = 2.0
        mx = (64 - mask.width * pitch) / 2
        my = (64 - mask.height * pitch) / 2
        for y in range(mask.height):
            for x in range(mask.width):
                if mask.getpixel((x,y)) >= 64:
                    s.ellipse(mx+x*pitch+1, my+y*pitch+1, .78, .78, '#F4F3ED')
        return s
    if theme=='sense':
        s.rect(5,6,54,52,s.gradient('base','#B8C1BC','#515D58'),8,'#18221D',1)
        s.rect(7,8,50,47,s.gradient('face','#FAFAF3','#C7CDC5'),6,'#EDF2E8',.7)
    elif theme=='touchwiz':
        s.ellipse(32,34,27,27,s.gradient('water','#4E9DC4','#0A4766'),'#A6E6F0',.7)
        s.ellipse(31,29,24,24,s.gradient('mist','#E7F9F6','#A2DBDF'))
    elif theme=='optimus':
        s.rect(6,6,53,53,'#1C1A16',9,alpha=.28)
        s.rect(5,4,54,53,s.gradient('paper','#FFFEF4','#E5DAC6'),8,'#C5B79C',.8)
        s.line(12,8,51,8,'#FFFFFF',1)
    elif theme=='xperia':
        s.rect(6,7,52,51,s.gradient('glass','#E7E7F3','#9DAAC1'),2,'#AEBAD0',.65)
        s.line(8,8,55,8,'#FFFFFF',.6)
    else:
        s.rect(4,5,56,55,s.gradient('metal','#EDF0EF','#6D8294'),4,'#344759',1)
        s.rect(7,8,50,48,'#DFEAF0',2,'#263D52',.7)
        s.line(10,58,54,58,'#ECF5FA',.7)
    colored = glyph.copy()
    visible=[pixel for pixel in colored.getdata() if pixel[3]>180]
    # White logotypes need their own brand ink against historical light materials.
    whites=sum(min(p[:3])>205 for p in visible)/max(1,len(visible))
    darks=sum(max(p[:3])<100 for p in visible)/max(1,len(visible))
    if whites>.38 and darks<.08:
        ink=tuple(min(175,c) for c in brand)
        colored.putdata([(*ink,p[3]) if min(p[:3])>190 else p for p in colored.getdata()])
    color_art(s,cells(colored,24 if theme=='razr' else 64,12 if theme=='razr' else 20),theme)
    if theme=='sense':
        s.path('M8,9 H56 V20 Q33,27 8,20 Z','#FFFFFF',alpha=.12)
        s.line(12,55,52,55,'#2B3E31',.6)
    elif theme=='touchwiz':
        s.path('M9,27 C12,8 45,4 55,27 Q32,18 9,27 Z','#FFFFFF',alpha=.18)
        s.ellipse(32,59,18,1.6,'#081F31',alpha=.25)
    elif theme=='xperia':
        s.path('M7,8 H57 L7,35 Z','#FFFFFF',alpha=.12)
        s.line(29,57,57,57,'#9ACBFA',.7)
    return s


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--archive',type=Path,required=True)
    parser.add_argument('--inventory',type=Path,required=True)
    parser.add_argument('--vector-resources',action='store_true',help='Export optional XML for inspection; production uses prerendered WebP')
    args=parser.parse_args()
    inventory=json.loads(args.inventory.read_text(encoding='utf-8'))
    destination=ROOT/'app/src/main/res/drawable';masters=ROOT/'artwork/installed-app-icons'
    destination.mkdir(parents=True,exist_ok=True);masters.mkdir(parents=True,exist_ok=True)
    entries=[];catalog=[]
    with zipfile.ZipFile(args.archive) as archive:
        metadata={e['package']:e for e in json.loads(archive.read('labels.json'))['entries']}
        assert {e['package'] for e in inventory['entries']}==set(metadata), 'Inventory changed; recapture references'
        for app in inventory['entries']:
            package=app['package'];key='app_'+hashlib.sha256(package.encode()).hexdigest()[:12]
            glyph=foreground(archive,package)
            monochrome=None
            mono_key=f"monochrome/{package}.png"
            if mono_key in archive.namelist():
                candidate=Image.open(io.BytesIO(archive.read(mono_key))).convert("RGBA")
                bounds=candidate.getbbox()
                if bounds is not None:monochrome=candidate.crop(bounds)
            original=Image.open(io.BytesIO(archive.read(f"icons/{package}.png"))).convert("RGBA")
            palette=Counter(tuple((v//24)*24+12 for v in pixel[:3]) for pixel in original.getdata() if pixel[3]>220 and max(pixel[:3])<225)
            brand=palette.most_common(1)[0][0] if palette else (30,70,95)
            if package in DOT_WORDMARKS:
                text=DOT_WORDMARKS[package];font=ImageFont.load_default(size=40)
                bounds=font.getbbox(text)
                wordmark=Image.new("RGBA",(bounds[2]-bounds[0]+2,bounds[3]-bounds[1]+2))
                ImageDraw.Draw(wordmark).text((1-bounds[0],1-bounds[1]),text,font=font,fill="white")
                monochrome=wordmark
            resources=[]
            for theme in THEMES:
                resource=f'aura_{theme}_{key}';scene=(families.make_icon(theme,COUNTERPARTS[package]) if package in COUNTERPARTS and theme in ('sense','touchwiz','optimus','xperia') else make(theme,glyph,monochrome,brand))
                folder=masters/theme;folder.mkdir(exist_ok=True)
                (folder/f'{key}.svg').write_text(scene.svg()+'\n',encoding='utf-8',newline='\n')
                if args.vector_resources:(destination/f'{resource}.xml').write_text(scene.vector(),encoding='utf-8',newline='\n')
                resources.append(resource)
            label=metadata[package]['label']
            entries.append(dict(package=package,label=label,key=key,resources=resources,provenance='Derived vector interpretation of the installed app icon',review='pending optical review'))
            quoted=json.dumps(package,ensure_ascii=False)
            catalog.append(f'    {quoted} to intArrayOf('+', '.join('R.drawable.'+r for r in resources)+'),')
    (masters/'manifest.json').write_text(json.dumps(dict(appCount=len(entries),themes=list(THEMES),variantCount=len(entries)*6,entries=entries),ensure_ascii=False,indent=2)+'\n',encoding='utf-8',newline='\n')
    source="""// Generated from a verified private Motorola inventory. Artwork is derived; see its manifest.
package dev.senda.launcher.ui.theme.icons

import androidx.annotation.DrawableRes
import dev.senda.launcher.R
import dev.senda.launcher.data.LauncherTheme

private val installedArtwork = mapOf(
"""+ '\n'.join(catalog)+"""
)

@DrawableRes fun installedAppArtwork(packageName: String, theme: LauncherTheme): Int? {
    if (theme == LauncherTheme.PS4) return ps4AppArtwork(packageName)
    if (theme == LauncherTheme.TOUCHWIZ) return s3AppArtwork(packageName)
    val slot = when (theme) {
        LauncherTheme.NOTHING -> 0; LauncherTheme.SENSE -> 1; LauncherTheme.TOUCHWIZ -> 2
        LauncherTheme.OPTIMUS -> 3; LauncherTheme.XPERIA -> 4; LauncherTheme.RAZR -> 5
        else -> return null
    }
    return installedArtwork[packageName]?.get(slot)
}
const val installedArtworkCount = """+str(len(entries))+'\n'
    source += '\ndata class InstalledArtworkEntry(val packageName: String, val label: String)\nval installedArtworkEntries = listOf(\n'+''.join('    InstalledArtworkEntry('+json.dumps(e['package'])+', '+json.dumps(e['label'],ensure_ascii=False)+'),\n' for e in sorted(entries,key=lambda e:e['label'].casefold()))+')\n'
    (ROOT/'app/src/main/java/dev/senda/launcher/ui/theme/icons/InstalledAppArtwork.kt').write_text(source,encoding='utf-8',newline='\n')
    print(f'Generated {len(entries)} app identities / {len(entries)*6} SVG variants; render production WebP next')


if __name__=='__main__':main()
