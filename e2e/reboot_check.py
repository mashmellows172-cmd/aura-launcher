import argparse,base64,json,re,xml.etree.ElementTree as ET
from device import Device,EVIDENCE

p=argparse.ArgumentParser();p.add_argument('--address',required=True);p.add_argument('action',choices=['prepare','verify']);a=p.parse_args()
d=Device(a.address)

def widget_ids(dump):
    # The host remains the same package/host ID across package updates and a reboot.
    return dump

if a.action=='prepare':
    d.shell('input','keyevent','KEYCODE_HOME')
    d.tap('Personalizar tema.');d.tap('Añadir widget');d.tap('Buscar widget')
    d.shell('input','text','digital');d.shell('input','keyevent','KEYCODE_BACK')
    d.tap('Digital')
    if d.contains('Crear'):d.tap('Crear')
    if d.contains('Elige el estilo del reloj'):d.tap('Transparente')
    assert d.contains('Tus widgets')
    prefs=d.shell('run-as','dev.senda.launcher.debug','cat','shared_prefs/aura_widgets.xml')
    (EVIDENCE/'pre-reboot-widgets.xml').write_text(prefs,encoding='utf-8')
    data=base64.b64decode(d.shell('run-as','dev.senda.launcher.debug','base64','files/datastore/senda_settings.preferences_pb'))
    (EVIDENCE/'preferences-after-qa.pb').write_bytes(data)
    boot=d.shell('cat','/proc/sys/kernel/random/boot_id').strip()
    (EVIDENCE/'pre-reboot-boot-id.txt').write_text(boot)
    d.capture('pre-reboot-clock')
    print('Preparado widget real para verificar recuperación tras reboot; aún no se ordena el reinicio.')
else:
    assert d.shell('getprop','sys.boot_completed').strip()=='1'
    assert d.shell('cat','/proc/sys/kernel/random/boot_id').strip()!=(EVIDENCE/'pre-reboot-boot-id.txt').read_text().strip()
    assert 'dev.senda.launcher.debug' in d.shell('cmd','role','get-role-holders','android.app.role.HOME')
    d.shell('input','keyevent','KEYCODE_HOME')
    assert d.contains('Tus widgets') and d.contains('Actual: Aura Pulse')
    dump=d.shell('dumpsys','appwidget')
    root=ET.fromstring((EVIDENCE/'pre-reboot-widgets.xml').read_text(encoding='utf-8'))
    ids=[entry.split(':')[0] for entry in root.find("string[@name='widgets']").text.split(';')]
    for widget_id in ids:assert re.search(r'(?:appWidgetId|id)\s*=\s*'+widget_id+r'\b',dump)
    assert 'com.google.android.deskclock' in dump
    d.capture('post-reboot-clock')
    d.tap('Editar widgets');d.tap('Quitar')
    assert not d.contains('Tus widgets')
    original=(EVIDENCE/'original-screen-timeout.txt').read_text().strip()
    if original=='null':d.shell('settings','delete','system','screen_off_timeout')
    else:d.shell('settings','put','system','screen_off_timeout',original)
    results=['REBOOT-H1: arranque completo, HOME, Aura Pulse y widget con ID conservado; widget de prueba retirado y timeout restaurado']
    (EVIDENCE/'reboot-results.json').write_text(json.dumps(results,ensure_ascii=False,indent=2),encoding='utf-8')
    print('PASS:',results[0])
