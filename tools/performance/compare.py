"""Compare real Macrobenchmark JSON. Missing/invalid evidence fails closed.
Usage: python tools/performance/compare.py baseline.json candidate.json
Does NOT replace the separate idle/battery and visual acceptance gates.
"""
import json, math, sys
from pathlib import Path

def measurements(path):
 data=json.loads(Path(path).read_text(encoding="utf-8"))
 context=data.get("context", {})
 if context.get("build",{}).get("device","").lower().startswith(("generic","emulator")):
  raise ValueError("Physical device required")
 if context.get("build",{}).get("brand","").lower() != "motorola" or context.get("isEmulator") is True:
  raise ValueError("Motorola physical-device metadata required")
 rows={r["name"].split("[")[0]:r for r in data["benchmarks"]}
 result={}
 for name, metric, percentile in [
  ("coldStartupUncompiled","timeToInitialDisplayMs",None),
  ("coldStartupUncompiled","timeToFullDisplayMs",None),
  ("coldStartupProfileRequired","timeToInitialDisplayMs",None),
  ("coldStartupProfileRequired","timeToFullDisplayMs",None),
  ("alphabetAndScroll","frameDurationCpuMs","P95"),
  ("alphabetAndScroll","frameOverrunMs","P95")]:
  row=next((r for n,r in rows.items() if n.endswith(name)),None)
  if not row:raise ValueError("Missing journey: "+name)
  if row.get("repeatIterations",0)<10:raise ValueError("At least ten iterations required")
  bucket=row.get("sampledMetrics" if percentile else "metrics",{})
  values=bucket[metric]
  value=values[percentile or "median"]
  if not isinstance(value,(int,float)) or not math.isfinite(value):raise ValueError("Invalid metric")
  result[name+"/"+metric]=value
 return context,result

def main():
 b,bm=measurements(sys.argv[1]);c,cm=measurements(sys.argv[2])
 for key in ("brand","model","device","fingerprint","version"):
  bv=b.get("build",{}).get(key);cv=c.get("build",{}).get(key)
  if bv!=cv:raise ValueError("Device/OS mismatch: "+key)
 if not b.get("build"):raise ValueError("Missing device metadata")
 failed=[]
 for key,base in bm.items():
  # Provisional budget: +10%, with a noise floor of 20ms startup / 1ms frame.
  tolerance=max(abs(base)*.10,1.0 if "frame" in key else 20.0)
  ok=cm[key]<=base+tolerance
  print(f"{key}: {base:.3f} -> {cm[key]:.3f} ms; {'PASS' if ok else 'FAIL'}")
  if not ok:failed.append(key)
 if failed:raise ValueError("Performance regression: "+", ".join(failed))
 print("Timing budget passed. Idle/battery, artifact identity and visual review still required.")
if __name__=="__main__":
 try:main()
 except (ValueError,KeyError,IndexError,OSError,StopIteration) as error:
  print("BLOCKED:",error,file=sys.stderr);sys.exit(1)
