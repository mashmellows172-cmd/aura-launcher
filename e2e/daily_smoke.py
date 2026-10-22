import argparse
import json
import xml.etree.ElementTree as ET
from device import Device, EVIDENCE

parser=argparse.ArgumentParser()
parser.add_argument('--address',required=True)
args=parser.parse_args()
d=Device(args.address)
results=[]

def passed(name):
    results.append(name)
    (EVIDENCE/'daily-results.json').write_text(json.dumps(results,ensure_ascii=False,indent=2),encoding='utf-8')
    print('PASS:',name,flush=True)

def widgets():
    xml=d.shell('run-as','dev.senda.launcher.debug','cat','shared_prefs/aura_widgets.xml')
    entry=ET.fromstring(xml).find("string[@name='widgets']")
    return [(int(pair.split(':')[0]),int(pair.split(':')[1])) for pair in (entry.text or '').split(';') if pair] if entry is not None else []

def home(): d.shell('input','keyevent','KEYCODE_HOME')
def restart():
    d.shell('am','force-stop','dev.senda.launcher.debug')
    d.shell('monkey','-p','dev.senda.launcher.debug','1')

home()
assert d.contains('Tus widgets')
initial=widgets()
assert len(initial)==1 and initial[0][0]==49, 'Unexpected widgets: leave user data alone'
d.tap('Editar widgets')
d.tap('Aumentar altura del widget')
expected=[(49,min(initial[0][1]+40,420))]
assert widgets()==expected
d.tap('Listo')
restart()
assert d.contains('Tus widgets') and widgets()==expected
d.capture('widget-persisted')
passed('F07-E2: altura e ID conservados después de reiniciar proceso')

for letter,app in [('W','WhatsApp'),('Y','YouTube'),('Z','Zip Extractor')]:
    d.tap('Saltar a la sección '+letter)
    assert d.contains(app)
passed('F02-H1: W, Y y Z accesibles con widget añadido')

home()
d.tap('Personalizar tema.')
d.tap('Tema visual')
d.scroll_to('AMOLED puro')
d.tap('AMOLED puro')
restart()
assert d.contains('Actual: AMOLED puro')
passed('F08-H1: AMOLED puro conserva identidad tras reiniciar proceso')
home()
d.tap('Personalizar tema.')
d.tap('Tema visual')
d.tap('Aura Pulse')
assert d.contains('Actual: Aura Pulse')

for shortcut,package in [('Abrir alarmas','com.google.android.deskclock'),('Abrir calendario','com.google.android.calendar')]:
    home()
    d.tap(shortcut)
    state=d.shell('dumpsys','activity','activities')
    top=next((line for line in state.splitlines() if 'topResumedActivity=' in line),'')
    assert package in top,top
    passed('F09-H1: '+shortcut+' abre '+package)
home()
d.tap('Abrir Prognoza')
state=d.shell('dumpsys','activity','activities')
top=next((line for line in state.splitlines() if 'topResumedActivity=' in line),'')
assert 'dev.senda.launcher' not in top and 'intentresolver' not in top,top
passed('F09-H1: clima abre aplicación elegida ('+top.strip()+')')
home()
d.capture('beta14-daily-pass')
