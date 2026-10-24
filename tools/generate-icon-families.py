"""Author Aura's original vector icon families. No third-party artwork or runtime SVG parser.

Run from any directory; generated SVG masters, Android resources and catalog share one scene.
Gradients, paths and optical bounds are authored here, rather than produced by a model at runtime.
"""
from pathlib import Path
from html import escape
import hashlib
import json
import math

ROOT = Path(__file__).resolve().parents[1]
THEMES = ('nothing', 'sense', 'touchwiz', 'optimus', 'xperia', 'razr')
LABELS = ('Nothing / CMF', 'Sense clásico', 'TouchWiz Nature', 'LG Optimus', 'Xperia Flow', 'RAZR V3')
FUNCTIONS = {
    'phone': 'Teléfono', 'message': 'Mensajes', 'camera': 'Cámara', 'browser': 'Navegador',
    'music': 'Música', 'calendar': 'Calendario', 'settings': 'Ajustes', 'files': 'Archivos',
    'contacts': 'Contactos', 'clock': 'Reloj', 'gallery': 'Galería', 'calculator': 'Calculadora',
    'email': 'Correo', 'video': 'Vídeo', 'store': 'Tienda', 'maps': 'Mapas',
    'notes': 'Notas', 'recorder': 'Grabadora', 'radio': 'Radio', 'weather': 'Clima',
    'downloads': 'Descargas', 'games': 'Juegos', 'health': 'Salud', 'navigation': 'Navegación',
    'documents': 'Documentos', 'reader': 'Lectura', 'cloud': 'Nube', 'security': 'Seguridad',
    'automation': 'Automatización', 'tools': 'Herramientas', 'communication': 'Comunicación', 'photography': 'Fotografía',
}
# Nine-cell symbolic drawings. Nothing uses circular light emitters; RAZR a framed LCD sprite.
PIXELS = {
    'phone': ['011000000','111000000','111000000','011100000','001110000','000111110','000011111','000001111','000000110'],
    'message': ['000000000','111111111','100000001','101111101','100000001','101110001','100000001','111011111','000110000'],
    'camera': ['000111000','001111100','111111111','100000001','100111001','101000101','101000101','100111001','111111111'],
    'browser': ['000111000','011010110','010010010','100010001','111111111','100010001','010010010','011010110','000111000'],
    'music': ['000011110','000010010','000010010','000010010','000010010','000010010','001110110','011110110','001100000'],
    'calendar': ['001000100','111111111','101000101','111111111','100000001','101010101','100000001','101010101','111111111'],
    'settings': ['000111000','010111010','011000110','110010011','110101011','110010011','011000110','010111010','000111000'],
    'files': ['000000000','111100000','100110000','100011111','111111111','100000001','100000001','100000001','111111111'],
    'contacts': ['000000000','000111000','001111100','001111100','000111000','000000000','001111100','011111110','111111111'],
    'clock': ['000111000','011000110','010010010','100010001','100011101','100000001','010000010','011000110','000111000'],
    'gallery': ['111111111','100000001','100001101','100001101','100000001','100100001','101010101','110001011','111111111'],
    'calculator': ['011111110','010000010','010111010','010000010','011111110','010101010','010101010','010101010','011111110'],
    'email': ['000000000','111111111','110000011','101000101','100101001','100010001','100000001','100000001','111111111'],
    'video': ['111111111','101010101','111111111','100000001','100100001','100110001','100111001','100110001','111111111'],
    'store': ['000111000','001000100','001000100','111111111','100000001','100000001','100000001','100000001','111111111'],
    'maps': ['000111000','001000100','010111010','010101010','010111010','001000100','000101000','000101000','000010000'],
    'notes': ['011111110','010000010','011111010','010000010','011111010','010000010','011110010','010000010','011111110'],
    'recorder': ['000111000','000101000','000101000','010101010','010111010','010000010','001111100','000010000','001111100'],
    'radio': ['000000110','000011000','001100000','111111111','100000001','101110101','101110001','101110101','111111111'],
    'weather': ['001000000','100010000','001110000','011111000','001111100','000110110','011000011','110000001','111111111'],
    'downloads': ['000010000','000010000','000010000','010010010','001010100','000111000','100010001','100000001','111111111'],
    'games': ['000000000','001111100','011111110','110000011','110100101','111110011','110100101','110000011','011000110'],
    'health': ['000000000','011000110','111101111','111111111','111111111','011111110','001111100','000111000','000010000'],
    'navigation': ['000010000','000111000','000111000','001111100','001111100','011111110','011000110','110000011','100000001'],
    'documents': ['011111000','010001100','010001110','010000010','011111010','010000010','011111010','010000010','011111110'],
    'reader': ['111101111','100111001','100111001','101111101','100111001','101111101','100111001','100111001','111101111'],
    'cloud': ['000000000','000111000','001000100','011000110','110000011','100000001','100000001','011111110','000000000'],
    'security': ['000010000','001111100','111111111','100000001','100010001','100111001','010111010','001000100','000111000'],
    'automation': ['001111100','010000010','100010001','000011000','000111000','000110000','100010001','010000010','001111100'],
    'tools': ['011000000','111000000','111100000','001110000','000111000','000011100','000001110','000000111','000000011'],
    'communication': ['111111000','100001000','101101000','100001000','111011111','001010001','001001101','001000001','001111111'],
    'photography': ['000111000','001000100','010000010','100111001','101000101','101000101','100111001','010000010','001111100'],
}


class Scene:
    def __init__(self):
        self.paths = []
        self.gradients = {}

    def gradient(self, name, first, last, horizontal=False):
        self.gradients[name] = (first, last, horizontal)
        return '@' + name

    def path(self, d, fill='none', stroke=None, width=1, alpha=1):
        self.paths.append(dict(d=d, fill=fill, stroke=stroke, width=width, alpha=alpha))

    def rect(self, x, y, w, h, fill, r=0, stroke=None, width=1, alpha=1):
        r = min(r, w/2, h/2)
        self.path(f'M{x+r},{y} H{x+w-r} Q{x+w},{y} {x+w},{y+r} V{y+h-r} Q{x+w},{y+h} {x+w-r},{y+h} H{x+r} Q{x},{y+h} {x},{y+h-r} V{y+r} Q{x},{y} {x+r},{y} Z', fill, stroke, width, alpha)

    def ellipse(self, cx, cy, rx, ry, fill, stroke=None, width=1, alpha=1):
        self.path(f'M{cx-rx},{cy} A{rx},{ry} 0 1,0 {cx+rx},{cy} A{rx},{ry} 0 1,0 {cx-rx},{cy} Z', fill, stroke, width, alpha)

    def line(self, x, y, xx, yy, color, width=2, alpha=1):
        self.path(f'M{x},{y} L{xx},{yy}', stroke=color, width=width, alpha=alpha)

    def svg(self):
        defs = ''.join(f'<linearGradient id="{n}" x1="8" y1="8" x2="{56 if h else 8}" y2="{8 if h else 56}" gradientUnits="userSpaceOnUse"><stop stop-color="{a}"/><stop offset="1" stop-color="{b}"/></linearGradient>' for n,(a,b,h) in self.gradients.items())
        paths = ''.join(f'<path d="{p["d"]}" fill="{self.svgpaint(p["fill"])}" stroke="{self.svgpaint(p["stroke"] or "none")}" stroke-width="{p["width"]}" stroke-linecap="round" stroke-linejoin="round" opacity="{p["alpha"]}"/>' for p in self.paths)
        return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64" width="64" height="64"><defs>{defs}</defs>{paths}</svg>'

    @staticmethod
    def svgpaint(paint):
        return f'url(#{paint[1:]})' if paint.startswith('@') else paint

    def vector(self):
        out = ['<vector xmlns:android="http://schemas.android.com/apk/res/android" xmlns:aapt="http://schemas.android.com/aapt" android:width="48dp" android:height="48dp" android:viewportWidth="64" android:viewportHeight="64">']
        for p in self.paths:
            fill = p['fill']; stroke = p['stroke']
            attrs = [f'android:pathData="{p["d"]}"', f'android:fillAlpha="{p["alpha"]}"']
            if not fill.startswith('@'): attrs.append(f'android:fillColor="{fill if fill != "none" else "#00000000"}"')
            if stroke:
                attrs += [f'android:strokeColor="{stroke}"', f'android:strokeWidth="{p["width"]}"', 'android:strokeLineCap="round"', 'android:strokeLineJoin="round"', f'android:strokeAlpha="{p["alpha"]}"']
            out.append('<path '+ ' '.join(attrs) + '>')
            if fill.startswith('@'):
                a,b,h = self.gradients[fill[1:]]
                out.append(f'<aapt:attr name="android:fillColor"><gradient android:type="linear" android:startX="8" android:startY="8" android:endX="{56 if h else 8}" android:endY="{8 if h else 56}" android:startColor="{a}" android:endColor="{b}"/></aapt:attr>')
            out.append('</path>')
        return '\n'.join(out+['</vector>'])+'\n'


def make_icon(theme, name):
    s = Scene()
    if theme in ('nothing', 'razr'):
        if theme == 'nothing':
            s.ellipse(32,32,29,29,'#171819', '#727574', .8)
            for y,row in enumerate(PIXELS[name]):
                for x,bit in enumerate(row):
                    if bit == '1': s.ellipse(16+x*4,16+y*4,1.42,1.42,'#F5F4EA')
        else:
            metal=s.gradient('bezel','#EDF1F0','#627C90')
            s.rect(4,7,56,51,metal,5,'#263543',1)
            s.rect(7,10,50,43,'#102C48',3,'#F1FBFF',.7)
            s.rect(10,13,44,37,s.gradient('lcd','#304F6E','#15324D'),1)
            for y,row in enumerate(PIXELS[name]):
                for x,bit in enumerate(row):
                    if bit == '1': s.rect(15.7+x*3.65,15.8+y*3.65,2.9,2.9,'#CDEFFF',.2)
            s.line(14,55,50,55,'#E3EDF4',.7)
        return s

    sense=theme=='sense'; water=theme=='touchwiz'; paper=theme=='optimus'; flow=theme=='xperia'
    primary = {'sense':'#73AD37','touchwiz':'#50BAE8','optimus':'#D56D23','xperia':'#A191F1'}[theme]
    dark = {'sense':'#344E24','touchwiz':'#12649D','optimus':'#9D431B','xperia':'#4A3D96'}[theme]
    if flow:
        primary, dark = {
            'phone': ('#81BA6C', '#376446'), 'music': ('#F7BB4A', '#D66A2B'),
            'gallery': ('#F5CE70', '#C58A39'), 'photography': ('#F5CE70', '#C58A39'),
            'contacts': ('#80BDD6', '#437D9D'), 'camera': ('#97BCCF', '#446C91'),
        }.get(name, (primary, dark))
    color = s.gradient('color',primary,dark)
    metal = s.gradient('metal', '#FAF9EF' if paper else '#F3F6F3', '#D4C9B5' if paper else '#87949B')
    glass = s.gradient('glass', '#BDEAF5' if water or sense else '#D8CEFF', '#347FAC' if water or sense else '#594AB5')
    if flow and name in ('phone', 'music', 'gallery', 'photography', 'contacts', 'camera'):
        glass = s.gradient('glass', primary, dark)
    ink = '#FDFDF8' if not paper else '#634A36'
    shadow = '#101C25'
    # Distinct foundations: HTC object shadow; Samsung floating blue glint; LG porcelain; Sony fine glass.
    if sense: s.ellipse(32,55,23,3,shadow,alpha=.35)
    if water: s.ellipse(32,54,22,2.5,'#75C6EA',alpha=.27)
    if paper:
        s.rect(5,7,54,52,'#786754',9,alpha=.32)
        s.rect(4,4,56,53,metal,9,'#FFF9EB',1)
        s.line(11,8,52,8,'#FFFFFF',1.2)
    if flow: s.ellipse(32,54,23,2,'#A5B3FF',alpha=.20)

    if name == 'camera':
        if sense:
            s.rect(8,19,49,31,metal,5,'#313D44',1.3);s.rect(7,23,10,26,'#303B3D',3)
            s.rect(17,13,17,8,'#3B4347',2);s.rect(40,22,10,5,'#EDF7EE',1)
            s.ellipse(32,35,16,16,'#27343C','#F4F5E9',1.2);s.ellipse(32,35,11,11,glass,'#0D1820',2)
            s.ellipse(29,32,3,3,'#EDF8F8',alpha=.8)
        elif water:
            s.rect(6,19,52,32,'#233A4A',7,'#9ACCD7',1);s.rect(16,13,15,8,metal,2)
            s.ellipse(33,35,17,17,'#DCECEE','#263B42',1);s.ellipse(33,35,13,13,glass,'#0F405E',2)
            s.ellipse(30,31,5,3,'#C7F8FD',alpha=.75);s.rect(48,23,6,4,'#E0F5DA',1)
        elif paper:
            s.rect(11,19,42,29,'#F9FCF7',5,'#B4AAA0',1);s.rect(17,14,14,7,color,2)
            s.ellipse(33,33,12,12,color,'#FFF9EA',2);s.ellipse(33,33,8,8,'#34627B','#73553B',1)
            s.ellipse(30,30,2.5,2.5,'#E5F8FA');s.rect(44,22,5,3,'#E8BA63',1)
        else:
            s.rect(5,21,54,29,glass,3,'#D4D5FA',.8);s.rect(13,16,15,7,'#D4D5F4',2)
            s.ellipse(33,35,15,15,'#262D43','#F0EFFC',1.2);s.ellipse(33,35,10,10,'#6187B0','#BDBCEE',1)
            s.ellipse(30,31,3,3,'#DFEAFE',alpha=.8);s.line(9,25,17,25,'#E0E6FF',1)
    elif name == 'phone':
        if sense: s.rect(7,7,50,49,color,9,'#DCEEC4',1)
        elif water: s.ellipse(31,32,24,24,s.gradient('green','#A3DC56','#3B8B2E'),'#DDF6B9',1)
        elif flow: s.rect(9,9,46,45,glass,5,'#DFD7FF',.8)
        handset='M19,15 C10,22 18,43 38,49 L47,40 L36,31 L30,36 C25,33 23,29 23,25 L28,20 Z'
        if water: handset='M17,16 C11,22 18,40 35,47 Q40,49 46,40 L36,33 L31,37 Q23,33 22,26 L27,22 Z'
        if flow: handset='M18,15 L26,20 L22,27 Q26,36 34,38 L40,33 L48,40 Q43,50 36,48 C19,43 11,24 18,15 Z'
        s.path(handset,'#4D9341' if paper else '#F8FBEF', '#3F6C2A' if paper else None,.8)
    elif name == 'message' or name == 'communication':
        if name=='communication':s.rect(22,11,35,28,glass,9 if water else 4,'#E8EDFD',.8)
        x=9 if sense else 7 if water else 13 if paper else 6
        y=16 if not paper else 18
        w=46 if water else 42 if sense else 38 if paper else 48
        bubble=f'M{x+6},{y} H{x+w-6} Q{x+w},{y} {x+w},{y+6} V{y+24} Q{x+w},{y+30} {x+w-6},{y+30} H{x+17} L{x+5},{y+38} L{x+8},{y+30} H{x+6} Q{x},{y+30} {x},{y+24} V{y+6} Q{x},{y} {x+6},{y} Z'
        s.path(bubble,metal if sense else s.gradient('bubble','#D2F49B','#77B13F') if water else color if paper else glass,'#FFFFFF' if flow else '#89A072',.8)
        if flow:
            for xx in (22,32,42):s.ellipse(xx,30,2,2,'#FFFFFF')
        else:
            for yy,length in ((y+10,25),(y+17,20),(y+24,14)):
                s.line(x+8,yy,x+8+length,yy,'#50633C' if sense or water else '#FFF3DF',2)
    elif name == 'browser':
        if sense:s.rect(7,11,50,42,metal,4,'#5A6B6F',1);s.rect(10,15,44,34,'#2B4D68',2)
        r=21 if not paper else 18
        s.ellipse(32,32,r,r,glass if not paper else s.gradient('world','#90CCE3','#39829F'),'#E5F7FF',1)
        if water or sense:
            s.path('M20,15 L29,14 L29,20 L23,24 L25,29 L18,31 L14,26 Z M37,28 L47,25 L49,33 L43,39 L40,49 L35,43 L35,36 L30,32 Z','#A8D57C' if water else '#ACBC9B')
        else:
            s.ellipse(32,32,r*.48,r,'none','#EEF7FA',1)
            for yy in (24,32,40):s.line(32-r*.86,yy,32+r*.86,yy,'#E9E6FF' if flow else '#ECF5F6',1)
        if flow:s.path('M23,37 L39,22 L34,40 L31,34 Z','#FCFBFF')
    elif name == 'music':
        if sense:
            s.rect(12,6,39,48,'#293A3F',5,'#B7CCB8',1);s.rect(16,10,31,26,glass,2)
            s.ellipse(31.5,44,7,7,'#E0E7DD','#607079',1);s.ellipse(31.5,44,2,2,'#738E8D')
        elif water:
            s.ellipse(31,31,24,24,s.gradient('disc','#CDDFE8','#4A729A'),'#EBF7FD',1)
            s.ellipse(31,31,16,16,'none','#A6D6E6',.8);s.ellipse(31,31,5,5,'#36536D','#DFEAF2',1)
        elif paper:
            s.ellipse(32,32,20,20,'#393638','#7D7465',1)
            for rr in (12,16,18):s.ellipse(32,32,rr,rr,'none','#7B6F61',.7)
            s.ellipse(32,32,7,7,color);s.ellipse(32,32,2,2,'#F4E8D2')
        else:
            s.ellipse(32,32,24,24,color,'#FFE5B2',.8)
            s.path('M13,34 C18,16 21,47 27,31 C32,16 35,47 41,30 Q45,21 51,28',stroke='#FFF6DA',width=3.5)
        if not paper and not flow:
            s.path('M28,18 L43,14 V37 Q43,43 36,43 Q30,42 33,37 Q35,34 39,35 V23 L31,25 V40 Q31,46 24,46 Q18,45 21,40 Q23,37 28,38 Z','#F7F5EE' if not water else '#176D9E')
    elif name == 'calendar':
        if sense:
            s.rect(9,13,46,40,'#33493F',3);s.path('M9,13 L51,9 L55,48 L13,53 Z',metal,'#7C8D86',1)
            s.path('M9,13 L51,9 L52,21 L10,25 Z','#A63F37')
        else:
            s.rect(10 if not paper else 13,12,44 if not paper else 38,41,metal if not flow else glass,7 if water else 3,'#C2D1D4',.8)
            s.rect(10 if not paper else 13,12,44 if not paper else 38,12,s.gradient('header','#6DBAE1','#347CA9') if water else color if paper else '#A594E8',3)
        for x in (21,42):s.rect(x-1.5,7,3,13,'#E3E7DD',1.5,'#6F7D7B',.7)
        if water:
            s.path('M24,30 H36 V35 H29 V38 H36 V46 H24 V42 H32 V40 H25 V36 H32 V34 H24 Z','#426877')
        else:
            for y in range(3):
                for x in range(3):s.rect(19+x*10,29+y*7,4,3,'#584B3C' if paper else '#F7F3FF' if flow else '#566760',.6)
            s.rect(28,35,6,5,color,1)
    elif name == 'settings' or name == 'tools' or name == 'automation':
        if sense:
            s.rect(9,12,46,40,metal,5,'#60747A',1)
            for y,xx in ((22,37),(32,23),(43,33)):
                s.line(16,y,48,y,'#536A70',2);s.rect(xx-3,y-4,7,8,'#DFE6DC',1,'#52666D',1)
        elif paper:
            for x,yy in ((20,30),(32,23),(44,38)):
                s.line(x,15,x,49,'#BFB09E',2);s.rect(x-4,yy-5,8,10,color,2,'#FFF2DD',1)
        elif name=='automation':
            s.ellipse(32,32,21,21,'none',primary,5)
            s.path('M33,13 L23,34 H32 L28,51 L43,28 H34 L40,13 Z',metal)
        else:
            count=8 if water else 10
            for i in range(count):
                a=i*math.tau/count
                s.line(32+17*math.cos(a),32+17*math.sin(a),32+24*math.cos(a),32+24*math.sin(a),'#B9C6CE' if water else '#B5ACED',6 if water else 4)
            s.ellipse(32,32,19,19,metal if water else glass,'#E2ECF6',1)
            s.ellipse(32,32,9,9,'#49677D' if water else '#37365E','#E1E8F2',1.6)
        if name=='automation' and (sense or paper):s.path('M34,12 L25,32 H33 L29,51 L44,27 H35 L41,12 Z','#F5D07D','#81643B',.8)
        if name=='tools':s.path('M41,14 L38,23 L30,26 L23,39 L17,44 L14,40 L20,34 L26,23 L26,17 L33,11 L32,20 L37,21 Z','#728495' if not paper else '#695F55','#F0F1E9',.8)
    elif name == 'files':
        if sense:
            s.path('M7,16 H26 L31,22 H56 V51 H7 Z',s.gradient('back','#E5C879','#947132'),'#856B3F',1)
            s.rect(13,21,38,24,'#E9E9D8',1);s.line(18,28,42,28,'#9DA99C',1)
            s.path('M5,29 H59 L52,53 H10 Z',s.gradient('front','#F6D99B','#B18138'),'#A9814A',1)
        elif water:
            s.path('M6,16 Q6,12 11,12 H25 L32,20 H54 Q58,20 58,25 V49 Q58,53 53,53 H10 Q6,53 6,49 Z',s.gradient('folder','#F9D979','#C78D2F'),'#FCE6AA',1)
            s.path('M7,28 Q31,22 57,27 V48 Q57,52 52,52 H12 Q7,52 7,48 Z','#F6C654')
        elif paper:
            s.rect(15,13,31,34,'#FBF7E8',2,'#BAAB97',1);s.rect(20,18,29,31,'#F2EDD9',2,'#C4B69D',1)
            s.path('M11,29 H53 L49,51 H14 Z',color,'#EABF80',1);s.line(20,40,41,40,'#FFE7C4',2)
        else:
            s.path('M7,18 H26 L32,23 H57 L53,51 H7 Z',glass,'#D6D0F9',1)
            s.path('M7,18 L17,12 H32 L37,20 H56 L57,24 H31 L26,19 Z','#DDD8F3')
            s.line(13,46,48,46,'#E4DFFC',.8)
    elif name == 'contacts':
        s.rect(12,9,39,46,color if not sense else s.gradient('book','#A96D38','#674323'),3,'#E6D2A7',1)
        if sense:
            s.rect(12,9,5,46,'#5C4530',1)
            for yy in (17,29,41):s.rect(50,yy,5,6,'#B5C3A3',1)
        s.ellipse(33,26,7,8,metal)
        s.path('M21,45 Q22,33 33,34 Q44,33 45,45 Z',metal)
    elif name == 'clock':
        if sense:s.rect(8,8,48,48,metal,6,'#55676D',1)
        s.ellipse(32,32,22 if not paper else 19,22 if not paper else 19,metal if water else '#F4F1E6' if sense or paper else glass,'#516E83' if not flow else '#DCD4FF',1.5)
        for i in range(12):
            a=i*math.tau/12;s.line(32+17*math.sin(a),32-17*math.cos(a),32+19*math.sin(a),32-19*math.cos(a),'#59686D' if not flow else '#F3EEFF',.8)
        s.line(32,32,24,22,'#4A5459' if not flow else '#FFFFFF',3);s.line(32,32,43,28,'#4A5459' if not flow else '#FFFFFF',2)
        s.line(32,35,33,17,'#BE533C',.8);s.ellipse(32,32,2,2,'#C66A48')
    elif name in ('gallery','photography'):
        if sense:s.path('M11,15 L49,8 L58,47 L20,55 Z','#EDEFE3','#758B7A',1)
        s.rect(9 if water else 13,16,43 if water else 38,33,metal if not flow else glass,3 if water else 1,'#DDE8EC',1)
        s.rect(14 if water else 17,20,33 if water else 30,22,glass,1)
        s.ellipse(39,25,4,4,'#FFE4A4')
        s.path('M17,42 L28,28 L35,36 L41,31 L47,42 Z','#508F5E' if water else '#7A699D' if flow else '#798F58')
        if name=='photography':s.ellipse(30,31,8,8,'none','#F2F4EB',2)
    elif name == 'calculator':
        s.rect(14,7,36,49,metal if sense else color if water else glass if flow else '#495354',5,'#D5E1DC',1)
        s.rect(19,12,26,12,'#B9C5A7' if sense or paper else '#BCE4ED',2,'#5B7177',.8)
        for y in range(3):
            for x in range(3):s.rect(20+x*9,29+y*8,6,5,'#F6F0DD' if not flow else '#D4C9F7',1)
        s.rect(38,45,6,5,'#C78546',1)
    elif name == 'email':
        s.path('M7,20 L32,9 L57,20 V51 H7 Z',color if flow else metal,'#B7C2C1',1)
        s.path('M7,21 L32,39 L57,21 V51 H7 Z',glass if water or flow else '#EADCC0' if paper else '#C6CEC5','#E8E8DD',.8)
        s.path('M7,51 L27,34 Q32,30 37,34 L57,51 Z',metal)
        s.line(10,23,31,37,primary,1)
    elif name == 'video':
        s.rect(7,18,50,36,glass if flow else '#314D61' if water else '#303A3F',4,'#BCCFD8',1)
        s.path('M6,16 L54,7 L57,19 L9,28 Z',metal,'#46575E',.8)
        for i in range(4):s.path(f'M{10+i*11},15 L{16+i*11},14 L{21+i*11},24 L{15+i*11},25 Z','#455768')
        s.path('M26,30 L43,39 L26,48 Z','#F3F5E5')
    elif name == 'store':
        s.rect(11,22,42,32,metal if sense or paper else glass,4,'#E4EAE7',1)
        s.path('M21,24 V19 C21,3 43,3 43,19 V24',stroke=primary,width=4)
        s.path('M28,31 L40,39 L28,47 Z',color)
    elif name in ('maps','navigation'):
        if name=='maps':
            s.path('M5,17 L21,11 L41,18 L58,11 V48 L42,55 L22,48 L5,54 Z',metal,'#B5C7B0',1)
            s.path('M21,11 V48 L42,55 V18 Z','#BAD0A4' if not flow else '#AC9BE3')
            s.path('M9,42 L25,29 L40,40 L55,23',stroke='#F6F1D5',width=4)
        if name=='navigation':s.ellipse(32,32,23,23,glass,'#D8E4EC',1)
        if name=='navigation':s.path('M32,12 L47,48 L32,40 L17,48 Z','#F5F2E3','#5A7184',1)
        else:
            s.path('M35,47 C19,29 20,16 35,16 C50,16 51,29 35,47 Z',color,'#F3E6D1',1)
            s.ellipse(35,26,4,4,'#F8F4D8')
    elif name in ('notes','documents','reader'):
        if name=='reader':
            s.path('M6,12 Q20,8 32,15 Q46,8 58,12 V51 Q45,48 32,54 Q19,48 6,51 Z',metal,'#8C8B78',1)
            s.line(32,16,32,52,primary,2)
            for yy in (22,29,36,43):
                s.line(12,yy,26,yy,'#A19B87',1);s.line(38,yy,51,yy,'#A19B87',1)
        else:
            s.rect(13,9,38,46,metal if sense or paper else glass,2,'#B4C4C6',1)
            s.rect(13,9,38,9,color,2)
            for yy in (25,32,39,46):s.line(20,yy,44,yy,'#9D9F8B' if sense or paper else '#EBEDFA',1)
            if name=='notes':s.path('M39,14 L46,17 L28,48 L21,52 L22,44 Z','#EAB865','#8F783E',.8)
            else:s.path('M40,9 L51,20 H40 Z','#F9FAEE')
    elif name in ('recorder','radio'):
        if name=='radio':
            s.rect(7,20,50,33,metal if sense else glass if not paper else '#C2935A',5,'#BDCFD5',1)
            s.line(13,20,46,6,'#D7DFDF',2)
            for yy in (30,35,40,45):s.line(14,yy,31,yy,'#4E6572',1.5)
            s.ellipse(44,38,7,7,color,'#F0EEE3',1)
            s.rect(12,24,39,3,'#526E76',1)
        else:
            s.rect(25,9,14,29,metal,7,'#718492',1)
            for yy in (16,21,26):s.line(28,yy,36,yy,'#778B93',1)
            s.path('M19,27 V30 C19,49 45,49 45,30 V27',stroke=primary,width=4)
            s.line(32,44,32,54,'#91A2AA',3);s.rect(22,52,20,4,metal,2)
    elif name == 'weather' or name == 'cloud':
        if name=='weather':
            for i in range(8):
                a=i*math.tau/8;s.line(23+15*math.cos(a),22+15*math.sin(a),23+20*math.cos(a),22+20*math.sin(a),'#EEC764',2)
            s.ellipse(23,22,12,12,s.gradient('sun','#FFF1AD','#E6A843'))
        s.path('M15,49 C3,47 5,32 17,32 C16,15 40,14 44,30 C60,26 64,48 49,49 Z',metal if sense else s.gradient('cloudpaper','#A8B7BD','#72868E') if paper else s.gradient('cloud','#FAFEF6','#9DC8E0') if water else glass,'#DCEAF0',.7)
    elif name == 'downloads':
        s.path('M9,36 L17,29 H47 L55,36 V53 H9 Z',metal,'#8EA3AB',1)
        s.path('M26,9 H38 V29 H47 L32,44 L17,29 H26 Z',color,'#E3F0DD',1)
        s.line(15,48,49,48,'#7A929C',1)
    elif name == 'games':
        s.path('M17,19 C5,20 2,49 10,51 Q15,52 22,42 H42 Q49,52 54,51 C62,49 59,20 47,19 Z',metal if sense or paper else glass,'#B6C5D0',1)
        s.rect(17,27,5,17,'#4C6576',1);s.rect(11,33,17,5,'#4C6576',1)
        s.ellipse(44,29,3,3,primary);s.ellipse(50,36,3,3,'#D68A73')
    elif name == 'health':
        s.path('M32,52 C0,32 6,9 22,13 Q29,14 32,21 Q37,10 47,13 C66,20 52,40 32,52 Z',s.gradient('heart','#EA9C92','#B84253'),'#F6DBCB',1)
        s.path('M12,32 H24 L28,23 L34,42 L39,32 H52',stroke='#FFF5E6',width=2)
    elif name == 'security':
        s.path('M32,6 Q44,14 55,15 V31 Q54,48 32,58 Q10,48 9,31 V15 Q21,14 32,6 Z',metal if sense or paper else glass,'#BAD1D4',1)
        s.path('M32,13 L48,20 V32 Q47,42 32,50 Q17,42 16,32 V20 Z',color)
        s.path('M23,31 L29,37 L41,25',stroke='#F4F6E6',width=3)
    else:
        raise ValueError(name)
    return s


def main():
    masters=ROOT/'artwork/icon-families'; drawables=ROOT/'app/src/main/res/drawable'
    kotlin=ROOT/'app/src/main/java/dev/senda/launcher/ui/theme/icons'
    masters.mkdir(parents=True,exist_ok=True);drawables.mkdir(parents=True,exist_ok=True);kotlin.mkdir(parents=True,exist_ok=True)
    records=[]; scenes={}
    for theme in THEMES:
        (masters/theme).mkdir(exist_ok=True)
        for name,label in FUNCTIONS.items():
            scene=make_icon(theme,name);scenes[theme,name]=scene
            svg=scene.svg()+'\n'; resource=f'aura_{theme}_{name}'
            (masters/theme/f'{name}.svg').write_text(svg,encoding='utf-8',newline='\n')
            (drawables/f'{resource}.xml').write_text(scene.vector(),encoding='utf-8',newline='\n')
            records.append(dict(theme=theme,function=name,label=label,resource=resource,author='Aura original',version=1,sha256=hashlib.sha256(svg.encode()).hexdigest()))
    (masters/'manifest.json').write_text(json.dumps(records,ensure_ascii=False,indent=2)+'\n',encoding='utf-8',newline='\n')
    rows=[]
    for index,(name,label) in enumerate(FUNCTIONS.items()):
        rows.append(f'    {name.upper()}("{label}", intArrayOf('+', '.join('R.drawable.aura_'+t+'_'+name for t in THEMES)+')),')
    source='''// Generated by tools/generate-icon-families.py. Edit the master, not this mapping.
package dev.senda.launcher.ui.theme.icons

import androidx.annotation.DrawableRes
import dev.senda.launcher.R
import dev.senda.launcher.data.LauncherTheme

enum class FamilyIcon(val label: String, private val resources: IntArray) {
'''+ '\n'.join(rows)+'''
    ;
    @DrawableRes fun resource(theme: LauncherTheme): Int = resources[when(theme) {
        LauncherTheme.NOTHING -> 0; LauncherTheme.SENSE -> 1; LauncherTheme.TOUCHWIZ -> 2
        LauncherTheme.OPTIMUS -> 3; LauncherTheme.XPERIA -> 4; LauncherTheme.RAZR -> 5
        else -> 0
    }]
}
'''
    (kotlin/'FamilyIcon.kt').write_text(source,encoding='utf-8',newline='\n')
    # A single portable master contact sheet, using the exact SVG scenes exported above.
    core=list(FUNCTIONS)[:8]; cells=[]
    for row,(theme,title) in enumerate(zip(THEMES,LABELS)):
        y=78+row*120
        cells.append(f'<text x="24" y="{y+37}" fill="#E7E5DC" font-size="15">{escape(title)}</text>')
        for col,name in enumerate(core):
            x=195+col*101
            svg=scenes[theme,name].svg().replace('<svg ',f'<svg x="{x}" y="{y}" ',1).replace('width="64" height="64"','width="64" height="64"',1)
            # SVG ids are per-cell namespaced to prevent gradient collisions in the contact sheet.
            prefix=f'{theme}_{name}_'
            for key in scenes[theme,name].gradients:
                svg=svg.replace(f'id="{key}"',f'id="{prefix}{key}"').replace(f'url(#{key})',f'url(#{prefix}{key})')
            cells.append(svg)
            cells.append(f'<text x="{x+32}" y="{y+85}" text-anchor="middle" fill="#B6BBB9" font-size="11">{escape(FUNCTIONS[name])}</text>')
    sheet='<svg xmlns="http://www.w3.org/2000/svg" width="1024" height="820" viewBox="0 0 1024 820"><rect width="1024" height="820" fill="#14191C"/><text x="24" y="34" fill="#F6F4E9" font-family="sans-serif" font-size="21">Aura · seis familias originales</text><text x="24" y="56" fill="#AAB9BC" font-family="sans-serif" font-size="12">Muestra de 48 variantes · reinterpretación de época · sin recursos de fabricantes</text><g font-family="sans-serif">'+''.join(cells)+'</g></svg>\n'
    (masters/'contact-sheet.svg').write_text(sheet,encoding='utf-8',newline='\n')
    print(f'Generated {len(records)} vectors and SVG masters; {len(FUNCTIONS)} functions, {len(THEMES)} families.')


if __name__=='__main__':main()
