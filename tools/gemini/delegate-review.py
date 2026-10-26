"""Run a bounded Gemini review with explicitly selected source and no tools.
Requires Google sign-in completed by the user. No keys are read/exported here.
Usage: python tools/gemini/delegate-review.py visual|preview
Outputs stay outside Git in the local work directory.
"""
import argparse, datetime, hashlib, json, shutil, subprocess
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
p=argparse.ArgumentParser();p.add_argument("task",choices=["visual","preview"]);a=p.parse_args()
source={
 "visual":[("app/src/main/java/dev/senda/launcher/ui/theme/AuraTheme.kt",None),("app/src/main/java/dev/senda/launcher/ui/theme/ThemeTypography.kt",None),("app/src/main/java/dev/senda/launcher/ui/ThemeLivePreview.kt",None),("app/src/main/java/dev/senda/launcher/ui/ThemeEditorDialog.kt",None)],
 "preview":[("app/src/main/java/dev/senda/launcher/ui/ThemeLivePreview.kt",None),("app/src/main/java/dev/senda/launcher/ui/ThemeEditorDialog.kt",None),("app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt",[(350,455),(480,520)]),("app/src/main/java/dev/senda/launcher/widgets/WidgetShelf.kt",[(1,100)]),("app/src/main/java/dev/senda/launcher/ui/HeaderPersonalization.kt",[(1,180)])]
}
cli=shutil.which("gemini.cmd") or shutil.which("gemini")
if not cli:raise SystemExit("Gemini CLI is not installed")
instructions=ROOT/"docs/delegation/gemini"
parts=[(instructions/(a.task+".md")).read_text(encoding="utf-8"),"SOURCE EXCERPTS (numbers are original lines; omitted code is unknown, not absent):"]
manifest=[]
for filename,ranges in source[a.task]:
 raw=(ROOT/filename).read_bytes();lines=raw.decode("utf-8").splitlines()
 manifest.append({"file":filename,"sha256":hashlib.sha256(raw).hexdigest(),"ranges":ranges})
 parts.append("\nFILE "+filename)
 for start,end in ranges or [(1,len(lines))]:parts.extend(f"{i}: {lines[i-1]}" for i in range(start,min(end,len(lines))+1))
context="\n".join(parts)
base=ROOT.parents[1]/"work/gemini-aura-review";base.mkdir(parents=True,exist_ok=True)
run=base/(a.task+"-"+datetime.datetime.now(datetime.timezone.utc).strftime("%Y%m%dT%H%M%SZ"));run.mkdir()
(run/"scope.json").write_text(json.dumps(manifest,indent=2),encoding="utf-8")
with (run/"result.json").open("wb") as output,(run/"stderr.log").open("wb") as errors:
 try:
  result=subprocess.run([cli,"--policy",str(instructions/"review.toml"),"--output-format","json","--prompt","Review the supplied source under its explicit task instructions. No tools. Respond in Spanish."],input=context.encode("utf-8"),stdout=output,stderr=errors,cwd=base,timeout=240)
  print("Exit:",result.returncode,"Output:",run)
  raise SystemExit(result.returncode)
 except subprocess.TimeoutExpired:raise SystemExit("Timed out after 240s; inspect local output before retrying")
