"""Read-only ADB evidence snapshot. Invoke before/after a controlled idle interval.
Never resets batterystats, changes settings or launches/locks the phone.
Usage: python idle-snapshot.py --adb PATH --serial SERIAL --out PRIVATE.json
"""
import argparse,datetime,json,subprocess
from pathlib import Path
p=argparse.ArgumentParser();p.add_argument("--adb",required=True);p.add_argument("--serial",required=True);p.add_argument("--out",required=True);a=p.parse_args()
def shell(*args):
 r=subprocess.run([a.adb,"-s",a.serial,"shell",*args],capture_output=True,text=True,timeout=25)
 return {"exit":r.returncode,"stdout":r.stdout,"stderr":r.stderr}
pkg="dev.senda.launcher.benchmark"
data={"utc":datetime.datetime.now(datetime.timezone.utc).isoformat(),"package":pkg}
for key,args in {"model":["getprop","ro.product.model"],"fingerprint":["getprop","ro.build.fingerprint"],"uptime":["cat","/proc/uptime"],"battery":["dumpsys","battery"],"thermal":["dumpsys","thermalservice"],"appBattery":["dumpsys","batterystats",pkg],"cpu":["dumpsys","cpuinfo"],"gfx":["dumpsys","gfxinfo",pkg]}.items():data[key]=shell(*args)
path=Path(a.out)
with path.open("x",encoding="utf-8") as f:json.dump(data,f,ensure_ascii=False,indent=2)
print("Private evidence saved. Battery charge delta is whole-device, not app-only energy.")
