import argparse,json,re
from device import Device,PROJECT,EVIDENCE

parser=argparse.ArgumentParser()
parser.add_argument('--address',required=True)
args=parser.parse_args()
d=Device(args.address)
results=[]
def passed(case):
    results.append(case)
    (EVIDENCE/'hidden-results.json').write_text(json.dumps(results,ensure_ascii=False,indent=2),encoding='utf-8')
    print('PASS:',case,flush=True)
def home():d.shell('input','keyevent','KEYCODE_HOME')
def search():
    home();d.tap('Buscar apps');d.shell('input','text','Aura%sQA%sProbe');d.shell('input','keyevent','KEYCODE_BACK')

package='dev.aura.qa.catalog'
try:
    d.run('-s',args.address,'install','-r',str(PROJECT/'qa-probe/build/outputs/apk/debug/qa-probe-debug.apk'))
    search()
    if d.contains('Añadir Aura QA Probe a favoritas'):d.tap('Añadir Aura QA Probe a favoritas')
    home();d.tap('Personalizar tema.');d.tap('Editar favoritas')
    d.scroll_to('Subir Aura QA Probe')
    d.tap('Subir Aura QA Probe');d.tap('Bajar Aura QA Probe');d.tap('Listo')
    passed('F04-H1: reordenar favoritas y recuperar orden original')
    search()
    n=next(n for n in d.snapshot() if n.get('text')=='Aura QA Probe' and n.get('class')!='android.widget.EditText')
    x1,y1,x2,y2=map(int,re.findall(r'\d+',n.get('bounds')))
    x,y=str((x1+x2)//2),str((y1+y2)//2)
    d.shell('input','swipe',x,y,x,y,'850')
    d.tap('Ocultar app')
    assert d.contains('Sin coincidencias')
    home();d.tap('Personalizar tema.');d.tap('Editar favoritas')
    controls=d.snapshot()
    names=[n.get('text') for n in controls if n.get('text') and n.get('text') not in ('Editar favoritas','↑','↓','×','Listo')]
    assert len(names)>1
    moving=names[-1]
    d.tap('Subir '+moving);d.tap('Bajar '+moving)
    restored=[n.get('text') for n in d.snapshot() if n.get('text') and n.get('text') not in ('Editar favoritas','↑','↓','×','Listo')]
    assert restored==names
    d.tap('Listo')
    passed('F04-E1: guardar favoritas mientras una favorita está oculta')
    d.tap('Personalizar tema.');d.tap('Apps ocultas')
    d.scroll_to('Mostrar Aura QA Probe')
    d.tap('Mostrar Aura QA Probe')
    d.tap('Listo')
    search()
    assert d.contains('Quitar Aura QA Probe de favoritas')
    passed('F10-H1: restaurar app oculta recupera su favorita')
    d.tap('Quitar Aura QA Probe de favoritas')
    home();d.tap('Buscar apps');d.shell('input','text','telefono');d.shell('input','keyevent','KEYCODE_BACK')
    assert d.contains('Teléfono')
    passed('F03-E1: búsqueda sin tilde encuentra Teléfono en dispositivo')
finally:
    try:d.run('-s',args.address,'uninstall',package)
    except RuntimeError:pass
    home()
