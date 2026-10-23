"""Focused native checks for the dashboard and weather controls. Keeps evidence outside source.

Run only while the user has released the unlocked phone. --restore supplies the public JSON
backup exported before QA, so temporary app settings can be restored through Aura's own UI.
"""
import argparse,json,re,subprocess,time,zipfile
from pathlib import Path
import device

def main():
    p=argparse.ArgumentParser();p.add_argument('--address',required=True);p.add_argument('--evidence',type=Path,required=True)
    p.add_argument('--restore',type=Path,required=True);p.add_argument('--state',type=Path,required=True);args=p.parse_args()
    device.EVIDENCE=args.evidence;d=device.Device(args.address);d.stash_pip=lambda:None
    original=json.loads(args.state.read_text());results=[]
    def capture(name):
        (args.evidence/(name+'.png')).write_bytes(subprocess.check_output([str(device.ADB),'-s',d.address,'exec-out','screencap','-p'],timeout=30))
    def menu(label):
        d.shell('input','keyevent','HOME');time.sleep(8)
        if d.contains('Portapapeles inteligente'):raise AssertionError('A system notification covers the launcher header; no tap performed')
        d.tap('Personalizar tema.');d.tap(label)
    def choose_theme(title):
        menu('Tema visual')
        button=next(n.get('text') for n in d.snapshot() if n.get('text','').endswith('▾'))
        d.tap(button);d.scroll_to(title);d.tap(title);d.tap('Aplicar tema');time.sleep(.5)
    def restore():
        d.shell('input','keyevent','HOME');time.sleep(.5)
        d.run('-s',d.address,'push',str(args.restore),'/sdcard/Download/'+args.restore.name)
        menu('Personalización y respaldo');d.tap('Respaldo');d.scroll_to('Importar respaldo');d.tap('Importar respaldo')
        d.tap('Buscar');d.shell('input','text',args.restore.name);d.shell('input','keyevent','ENTER');time.sleep(.5);d.tap(args.restore.name)
        d.tap('Restaurar ajustes')
        for _ in range(15):
            if d.contains('Ajustes restaurados.'):break
            time.sleep(1)
        else:raise AssertionError('Restore did not finish')
        d.shell('input','keyevent','HOME')
    changed=False
    try:
        assert 'showing=false' in d.shell('dumpsys','window','policy'),'Locked phone'
        d.shell('settings','put','system','screen_off_timeout','1800000')
        menu('Organizar inicio');assert d.contains('Elige un bloque')
        d.capture('editor-top')
        assert not d.contains('VISTA PREVIA'),'Preview should initially be collapsed'
        d.tap('Agenda');d.tap('Posición de Agenda');d.tap('Al final de las apps')
        assert any(n.get('text')=='Deshacer' and n.get('enabled')=='true' for n in d.snapshot())
        d.tap('Deshacer');d.capture('editor-controls');d.tap('Cancelar');results.append('editor_select_change_undo_cancel')
        # Change only Aura's orientation setting, then rotate the live draft.
        changed=True;menu('Inicio y gestos');d.scroll_to('Seguir el sistema');d.tap('Seguir el sistema');d.tap('Listo')
        menu('Organizar inicio');d.tap('Agenda');d.tap('Posición de Agenda');d.tap('Al final de las apps')
        d.shell('settings','put','system','accelerometer_rotation','0');d.shell('settings','put','system','user_rotation','1');time.sleep(1)
        d.capture('editor-landscape-before-scroll')
        d.scroll_to('Al final de las apps')
        d.capture('editor-landscape');d.tap('Deshacer');d.tap('Cancelar');results.append('draft_rotation_undo_cancel')
        d.shell('settings','put','system','user_rotation','0');time.sleep(.5)
        d.shell('settings','put','system','font_scale','2.0');time.sleep(1)
        menu('Organizar inicio');d.tap('Widgets');d.capture('editor-large-text');d.tap('Cancelar');results.append('large_text_editor')
        d.shell('settings','put','system','font_scale',original['font_scale']);time.sleep(1)
        for title,key in [('Aura · Nothing','nothing'),('Aura Bloom · Sense','sense'),('Aura Prism · TouchWiz','touchwiz'),('Aura Flow · LG','lg'),('Aura Horizon · Xperia','xperia'),('Aura Alloy · RAZR V3','razr')]:
            choose_theme(title);capture(key+'-home');results.append('theme_'+key)
        invalid=args.evidence/'Aura-invalid-beta23.zip'
        with zipfile.ZipFile(invalid,'w') as archive:archive.writestr('../escape.png',b'invalid')
        d.run('-s',d.address,'push',str(invalid),'/sdcard/Download/'+invalid.name)
        menu('Tema visual')
        button=next(n.get('text') for n in d.snapshot() if n.get('text','').endswith('▾'))
        d.tap(button);d.scroll_to('Aura Bloom · Sense');d.tap('Aura Bloom · Sense')
        before=[n.get('text') for n in d.snapshot() if 'fondos ·' in n.get('text','')]
        d.scroll_to('colección retro (ZIP)');d.tap('colección retro (ZIP)');d.tap('Buscar')
        d.shell('input','text',invalid.name);d.shell('input','keyevent','ENTER');time.sleep(.5);d.tap(invalid.name)
        d.scroll_to('No se pudo importar:');assert d.contains('No se pudo importar:')
        after=[n.get('text') for n in d.snapshot() if 'fondos ·' in n.get('text','')]
        assert before==after,'Library summary changed after invalid import'
        d.capture('invalid-library-rejected');d.tap('Cancelar');results.append('invalid_library_rejected_previous_summary_retained')
        menu('Clima en el reloj');d.scroll_to('Animaciones del clima');d.scroll_to('Repetición');d.tap('Repetición');d.tap('Listo')
        time.sleep(2);capture('weather-sweep');results.append('weather_repeat_controls')
    finally:
        try:
            d.shell('settings','put','system','font_scale',original['font_scale'])
            d.shell('settings','put','system','user_rotation','0')
            if changed:restore()
        finally:
            for key in ['font_scale','accelerometer_rotation','user_rotation','screen_off_timeout']:
                value=original[key]
                if value=='null':d.shell('settings','delete','system',key)
                else:d.shell('settings','put','system',key,value)
            (args.evidence/'smoke-results.json').write_text(json.dumps({'passed':results},indent=2))
    print(json.dumps({'passed':results}),flush=True)

if __name__=='__main__':main()
