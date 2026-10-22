"""Android device QA by accessible text/description; no dependency on screen coordinates."""
import argparse
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET

PROJECT = Path(__file__).resolve().parents[1]
ADB = PROJECT.parents[1] / 'work/android-toolchain/android-sdk/platform-tools/adb.exe'
EVIDENCE = PROJECT.parents[1] / 'work/qa-beta14'

class Device:
    def __init__(self, address):
        self.address = address
        EVIDENCE.mkdir(parents=True, exist_ok=True)
        result = self.run('connect', address)
        if 'connected' not in result: raise RuntimeError(result)
        # A fresh Wi-Fi ADB connection shows a System UI heads-up over the header.
        # Wait for it to disappear before tapping; otherwise it can disable debugging.
        time.sleep(8)

    def run(self, *args):
        result = subprocess.run([str(ADB), *args], capture_output=True, timeout=45)
        if result.returncode: raise RuntimeError(result.stderr.decode(errors='replace') + result.stdout.decode(errors='replace'))
        return result.stdout.decode('utf-8', errors='replace')

    def shell(self, *args): return self.run('-s', self.address, 'shell', *args)

    def snapshot(self, name='current'):
        remote = f'/sdcard/aura-qa-{time.time_ns()}.xml'
        result = self.shell('uiautomator', 'dump', remote)
        if 'dumped to:' not in result:
            raise RuntimeError('UI Automator did not produce a fresh hierarchy: ' + result)
        xml = self.shell('cat', remote)
        self.shell('rm', remote)
        (EVIDENCE / f'{name}.xml').write_text(xml, encoding='utf-8')
        self.nodes = list(ET.fromstring(xml).iter('node'))
        return self.nodes

    def tap(self, label):
        self.stash_pip()
        nodes = self.snapshot()
        exact = [n for n in nodes if label in (n.get('text'), n.get('content-desc'))]
        candidates = exact or [n for n in nodes if label in (n.get('text', '') + n.get('content-desc', ''))]
        candidates.sort(key=lambda n: (n.get('class') in ('android.widget.EditText', 'android.widget.AutoCompleteTextView'), n.get('content-desc') != label, n.get('clickable') != 'true'))
        if not candidates: raise AssertionError(f'No visible control: {label}')
        node = candidates[0]
        x1, y1, x2, y2 = map(int, re.findall(r'\d+', node.get('bounds')))
        self.shell('input', 'tap', str((x1+x2)//2), str((y1+y2)//2))
        time.sleep(.4)

    def stash_pip(self):
        activity=self.shell('dumpsys','activity','activities')
        bounds=re.search(r'mode=pinned[^\n]*\n\s*mBounds=Rect\((-?\d+), (-?\d+) - (-?\d+), (-?\d+)\)',activity)
        if bounds:
            x1,y1,x2,y2=map(int,bounds.groups())
            if x1>=0 and x2>80:
                self.shell('input','swipe',str((x1+x2)//2),str((y1+y2)//2),'-400',str((y1+y2)//2),'800')
                time.sleep(.5)

    def contains(self, text):
        return any(text in (n.get('text', '') + n.get('content-desc', '')) for n in self.snapshot())

    def scroll_to(self, label):
        for _ in range(8):
            if self.contains(label): return
            candidates = [n for n in self.nodes if n.get('scrollable') == 'true']
            if not candidates: break
            x1,y1,x2,y2 = map(int, re.findall(r'\d+', candidates[0].get('bounds')))
            self.shell('input','swipe',str((x1+x2)//2),str(y1+(y2-y1)*4//5),str((x1+x2)//2),str(y1+(y2-y1)//5),'400')
        raise AssertionError('Not found after scrolling: ' + label)

    def capture(self, name):
        self.snapshot(name)
        image = subprocess.run([str(ADB), '-s', self.address, 'exec-out', 'screencap', '-p'], capture_output=True, timeout=20, check=True).stdout
        (EVIDENCE / f'{name}.png').write_bytes(image)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--address', required=True)
    parser.add_argument('action', choices=['snapshot','tap','type','key','capture','assert'])
    parser.add_argument('value', nargs='?', default='current')
    args = parser.parse_args()
    d = Device(args.address)
    if args.action == 'tap': d.tap(args.value)
    elif args.action == 'type': d.shell('input', 'text', args.value.replace(' ', '%s'))
    elif args.action == 'key': d.shell('input', 'keyevent', args.value)
    elif args.action == 'assert': assert d.contains(args.value), args.value
    elif args.action == 'capture': d.capture(args.value)
    if args.action == 'snapshot':
        for n in d.snapshot(args.value):
            if n.get('text') or n.get('content-desc'):
                print(n.get('text') or n.get('content-desc'), n.get('bounds'))
