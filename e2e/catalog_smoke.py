"""Exercise catalog package broadcasts with a disposable app, preserving the user's apps."""
import argparse
import json
import re
import time
from device import Device, PROJECT, EVIDENCE

parser = argparse.ArgumentParser()
parser.add_argument('--address', required=True)
args = parser.parse_args()
d = Device(args.address)
package = 'dev.aura.qa.catalog'
results = []

def passed(case):
    results.append(case)
    (EVIDENCE / 'catalog-results.json').write_text(json.dumps(results, ensure_ascii=False, indent=2), encoding='utf-8')
    print('PASS:', case, flush=True)

def search():
    d.shell('input', 'keyevent', 'KEYCODE_HOME')
    d.tap('Buscar apps')
    d.shell('input', 'text', 'Aura% sQA% sProbe'.replace('% s', '%s'))
    d.shell('input', 'keyevent', 'KEYCODE_BACK')

try:
    d.shell('input', 'keyevent', 'KEYCODE_HOME')
    d.run('-s', args.address, 'install', '-r', str(PROJECT / 'qa-probe/build/outputs/apk/debug/qa-probe-debug.apk'))
    search()
    assert d.contains('Añadir Aura QA Probe a favoritas') or d.contains('Quitar Aura QA Probe de favoritas')
    passed('F02-E1: app instalada aparece sin reiniciar Aura')
    if d.contains('Quitar Aura QA Probe de favoritas'): d.tap('Quitar Aura QA Probe de favoritas')
    d.tap('Añadir Aura QA Probe a favoritas')
    assert d.contains('Quitar Aura QA Probe de favoritas')
    passed('F04-H1: añadir favorita')
    d.shell('pm', 'disable-user', '--user', '0', package)
    assert d.contains('Sin coincidencias')
    passed('F02-E1: broadcast de desactivación actualiza catálogo')
    d.shell('pm', 'enable', package)
    assert d.contains('Quitar Aura QA Probe de favoritas')
    passed('F04-E1: favorita persiste tras desactivar y reactivar paquete')
    d.tap('Aura QA Probe')
    assert d.contains('Aura QA Probe: apertura correcta')
    d.shell('input', 'keyevent', 'KEYCODE_HOME')
    assert d.contains('Buscar apps') and d.contains('Tus widgets')
    passed('F01-H1/F03-H1: abrir app y HOME limpia búsqueda')
    search()
    d.tap('Quitar Aura QA Probe de favoritas')
    d.run('-s', args.address, 'uninstall', package)
    assert d.contains('Sin coincidencias')
    passed('F02-E1: desinstalación actualiza búsqueda sin reiniciar')
    d.shell('input', 'keyevent', 'KEYCODE_HOME')
    d.capture('beta14-catalog-pass')
finally:
    # Only the disposable fixture is removed, even after a failed assertion.
    try:
        d.shell('pm', 'enable', package)
        search()
        if d.contains('Quitar Aura QA Probe de favoritas'): d.tap('Quitar Aura QA Probe de favoritas')
    except (RuntimeError, AssertionError): pass
    try: d.run('-s', args.address, 'uninstall', package)
    except RuntimeError: pass
    d.shell('input', 'keyevent', 'KEYCODE_HOME')
