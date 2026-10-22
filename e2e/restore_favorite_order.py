import argparse,json,xml.etree.ElementTree as ET
from device import Device,EVIDENCE
p=argparse.ArgumentParser();p.add_argument('--address',required=True);a=p.parse_args()
d=Device(a.address)
from preferences import read_preferences
package_labels={'com.google.android.youtube':'YouTube','com.google.android.apps.youtube.music':'YouTube Music','com.vivaldi.browser':'Vivaldi','com.google.android.dialer':'Teléfono'}
original=read_preferences(EVIDENCE/'preferences-before.pb')['favorite_packages'].splitlines()
expected=[package_labels[package] for package in original]

def labels():
    return [n.get('text') for n in d.snapshot() if n.get('text') and n.get('text') not in ('Editar favoritas','↑','↓','×','Listo')]
if not d.contains('Editar favoritas'):
    d.shell('input','keyevent','KEYCODE_HOME');d.tap('Personalizar tema.');d.tap('Editar favoritas')
current=labels()
assert set(current)==set(expected) and len(current)==len(expected),'Unexpected user edits: refuse to overwrite favorites'
for i,name in enumerate(expected):
    while labels().index(name)>i:d.tap('Subir '+name)
assert labels()==expected
d.tap('Listo');d.shell('input','keyevent','KEYCODE_HOME')
path=EVIDENCE/'preferences-comparison.json'
report=json.loads(path.read_text())
report['favorite_order_restored_via_ui']=True
report['favorite_labels_match_original']=True
path.write_text(json.dumps(report,indent=2))
print('PASS: auditoría del orden de favoritas contra la captura original, sin añadir ni quitar favoritas del usuario')
