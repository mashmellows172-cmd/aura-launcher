"""Run only on the QA session whose sole pre-existing test widget is ID 49."""
import argparse,json,xml.etree.ElementTree as ET
from device import Device,PROJECT,EVIDENCE

parser=argparse.ArgumentParser()
parser.add_argument('--address',required=True)
args=parser.parse_args()
d=Device(args.address)
results=[]

def passed(name):
    results.append(name)
    (EVIDENCE/'widget-results.json').write_text(json.dumps(results,ensure_ascii=False,indent=2),encoding='utf-8')
    print('PASS:',name,flush=True)

def state():
    root=ET.fromstring(d.shell('run-as','dev.senda.launcher.debug','cat','shared_prefs/aura_widgets.xml'))
    entry=root.find("string[@name='widgets']")
    widgets=[tuple(map(int,p.split(':'))) for p in (entry.text or '').split(';') if p] if entry is not None else []
    pending=int(root.find("int[@name='pending']").get('value'))
    return widgets,pending

def home(): d.shell('input','keyevent','KEYCODE_HOME')

def select(name,query):
    home()
    d.tap('Personalizar tema.')
    d.tap('Añadir widget')
    d.tap('Buscar widget')
    d.shell('input','text',query)
    d.shell('input','keyevent','KEYCODE_BACK')
    d.tap(name)

def add_clock():
    before,_=state()
    select('Digital','digital')
    if d.contains('Crear'): d.tap('Crear')
    if d.contains('Elige el estilo del reloj'): d.tap('Transparente')
    after,pending=state()
    assert len(after)==len(before)+1 and pending==0
    assert d.contains('Tus widgets'), 'New widget must be visible without pressing HOME'

home()
original,_=state()
assert len(original)==1 and original[0][0]==49,'Refuse to alter widgets outside the known QA fixture'
select('Digital','digital')
assert d.contains('Crear')
d.tap('Cancelar')
assert state()==(original,0)
passed('F07-E1: cancelar permiso libera ID sin cambiar widgets')

select('Digital','digital')
d.tap('Crear')
assert d.contains('Elige el estilo del reloj')
d.shell('input','keyevent','KEYCODE_BACK')
assert state()==(original,0)
passed('F07-E1: cancelar configuración del proveedor libera ID')

add_clock()
two,_=state()
d.tap('Editar widgets')
d.tap('Bajar')
reordered,_=state()
assert reordered==list(reversed(two))
d.tap('Listo')
d.shell('am','force-stop','dev.senda.launcher.debug')
d.shell('monkey','-p','dev.senda.launcher.debug','1')
assert state()==(reordered,0) and d.contains('Tus widgets')
passed('F07-H1/F07-E2: añadir visible, reordenar y conservar IDs tras reabrir')

add_clock()
add_clock()
four,_=state()
assert len(four)==4
select('Digital','digital')
assert state()==(four,0)
passed('F07-E4: límite de cuatro no reserva un quinto ID')

home()
while state()[0]:
    if d.contains('Editar widgets'): d.tap('Editar widgets')
    d.tap('Quitar')
assert state()==([],0)
passed('F07-E2: quitar todos los widgets de prueba limpia sus referencias')

d.run('-s',args.address,'install','-r',str(PROJECT/'qa-probe/build/outputs/apk/debug/qa-probe-debug.apk'))
select('Aura QA Probe','Aura%sQA%sProbe')
d.tap('Crear')
assert d.contains('Aura QA Widget')
d.run('-s',args.address,'uninstall','dev.aura.qa.catalog')
assert d.contains('Widget no disponible')
d.tap('Quitar widget no disponible')
assert state()==([],0)
home()
d.capture('beta14-widget-cleanup-pass')
passed('F07-E3: desinstalar proveedor muestra estado recuperable; se elimina sin cierre')
