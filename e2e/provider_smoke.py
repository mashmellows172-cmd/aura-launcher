import argparse,json
from device import Device,PROJECT,EVIDENCE

p=argparse.ArgumentParser();p.add_argument('--address',required=True);a=p.parse_args()
d=Device(a.address)
d.shell('input','keyevent','KEYCODE_HOME')
if d.contains('Widget no disponible'):
    d.tap('Quitar widget no disponible') # The QA widget left by the reproduced provider-removal defect.
d.run('-s',a.address,'install','-r',str(PROJECT/'qa-probe/build/outputs/apk/debug/qa-probe-debug.apk'))
d.tap('Personalizar tema.');d.tap('Añadir widget');d.tap('Buscar widget')
d.shell('input','text','Aura%sQA%sProbe');d.shell('input','keyevent','KEYCODE_BACK')
d.tap('Aura QA Probe');d.tap('Crear')
assert d.contains('Aura QA Widget')
d.run('-s',a.address,'uninstall','dev.aura.qa.catalog')
for _ in range(4):
    if d.contains('Widget no disponible'):break
else:raise AssertionError('Provider removal did not invalidate the widget UI')
d.capture('beta14-provider-removed')
d.tap('Quitar widget no disponible')
d.shell('input','keyevent','KEYCODE_HOME')
assert not d.contains('Widget no disponible')
path=EVIDENCE/'widget-results.json'
results=json.loads(path.read_text(encoding='utf-8')) if path.exists() else []
case='F07-E3: retirar proveedor actualiza aviso sin reiniciar; quitar lo recupera'
results.append(case)
path.write_text(json.dumps(results,ensure_ascii=False,indent=2),encoding='utf-8')
print('PASS:',case,flush=True)
