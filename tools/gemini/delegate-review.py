"""Run a bounded Google/Antigravity review with explicitly selected source.
Requires Google sign-in completed by the user. No keys are read/exported here.
Usage: python tools/gemini/delegate-review.py visual|preview --model claude-sonnet-4-6
Gemini policy denies tools; Antigravity uses a custom review role and an explicit no-tools assignment.
AGY1.4.7 still advertises tools for tools: []; audit actual stream events.
Antigravity outputs must be checked for actual tool use before acceptance.
Outputs stay outside Git in the local work directory.
"""
import argparse, datetime, hashlib, json, shutil, subprocess, tempfile, os
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
p=argparse.ArgumentParser();p.add_argument("task",choices=["visual","preview"])
p.add_argument("--backend",choices=["antigravity","gemini"],default="antigravity")
p.add_argument("--model",choices=["claude-sonnet-4-6","gpt-oss-120b-medium"])
a=p.parse_args()
if a.backend=="antigravity" and not a.model:p.error("Antigravity requires an explicit approved model")
source={
 "visual":[("app/src/main/java/dev/senda/launcher/ui/theme/AuraTheme.kt",None),("app/src/main/java/dev/senda/launcher/ui/theme/ThemeTypography.kt",None),("app/src/main/java/dev/senda/launcher/ui/ThemeLivePreview.kt",None),("app/src/main/java/dev/senda/launcher/ui/ThemeEditorDialog.kt",None)],
 "preview":[("app/src/main/java/dev/senda/launcher/ui/ThemeLivePreview.kt",None),("app/src/main/java/dev/senda/launcher/ui/ThemeEditorDialog.kt",None),("app/src/main/java/dev/senda/launcher/ui/HomeScreen.kt",[(350,455),(480,520)]),("app/src/main/java/dev/senda/launcher/widgets/WidgetShelf.kt",[(1,100)]),("app/src/main/java/dev/senda/launcher/ui/HeaderPersonalization.kt",[(1,180)])]
}
if a.backend=="antigravity":
 cli=shutil.which("agy") or str(Path(os.environ["LOCALAPPDATA"])/"agy/bin/agy.exe")
else:cli=shutil.which("gemini.cmd") or shutil.which("gemini")
if not cli or not Path(cli).is_file():raise SystemExit("Selected CLI is not installed")
instructions=ROOT/"docs/delegation/gemini"
parts=[(instructions/(a.task+".md")).read_text(encoding="utf-8"),"SOURCE EXCERPTS (numbers are original lines; omitted code is unknown, not absent):"]
manifest=[]
for filename,ranges in source[a.task]:
 raw=(ROOT/filename).read_bytes()
 try:decoded=raw.decode("utf-8");encoding="utf-8"
 except UnicodeDecodeError:
  # Preserve valid UTF-8 spans; decode only isolated legacy bytes as CP1252.
  text=raw.decode("utf-8",errors="surrogateescape")
  decoded="".join(bytes([ord(c)-0xDC00]).decode("cp1252") if 0xDC80<=ord(c)<=0xDCFF else c for c in text)
  encoding="utf-8-with-isolated-cp1252-bytes"
 lines=decoded.splitlines()
 manifest.append({"file":filename,"sha256":hashlib.sha256(raw).hexdigest(),"ranges":ranges,"encoding":encoding})
 parts.append("\nFILE "+filename)
 for start,end in ranges or [(1,len(lines))]:parts.extend(f"{i}: {lines[i-1]}" for i in range(start,min(end,len(lines))+1))
context="\n".join(parts)
base=ROOT.parents[1]/"work/gemini-aura-review";base.mkdir(parents=True,exist_ok=True)
run=base/(a.backend+"-"+a.task+"-"+datetime.datetime.now(datetime.timezone.utc).strftime("%Y%m%dT%H%M%SZ"));run.mkdir()
(run/"scope.json").write_text(json.dumps(manifest,indent=2),encoding="utf-8")
with (run/"result.json").open("wb") as output,(run/"stderr.log").open("wb") as errors:
 try:
  with tempfile.TemporaryDirectory(prefix="aura-review-") as isolated:
   if a.backend=="antigravity":
    # A scoped role; AGY currently advertises tools even with tools: [].
    # Inspect stream step types; do not claim an enforced tool sandbox.
    agent=Path(isolated)/".agents/agents";agent.mkdir(parents=True)
    (agent/"aura-review.md").write_text("---\nname: aura-review\ndescription: Review only supplied source, no tools or planning artifacts.\ntools: []\nmainAgent: true\nsubagent: false\ncommandExecutionPolicy: off\n---\nReturn the completed review directly in the final response. No tools, edits, artifacts or follow-up questions. Base findings only on supplied excerpts.\n",encoding="utf-8")
    args=[cli,"--model",a.model,"--agent","aura-review","--disable-slash-commands","--input-format","stream-json","--output-format","stream-json","--print-timeout","180s"]
    payload=json.dumps({"event":"user","message":{"content":context}},ensure_ascii=False)+"\n"
   else:
    args=[cli,"--policy",str(instructions/"review.toml"),"--output-format","json","--prompt","Review the supplied source under its explicit task instructions. No tools. Respond in Spanish."]
    payload=context
   result=subprocess.run(args,input=payload.encode("utf-8"),stdout=output,stderr=errors,cwd=isolated,timeout=240)
  if a.backend=="antigravity" and result.returncode==0:
   output.flush()
   events=[json.loads(line) for line in (run/"result.json").read_text(encoding="utf-8").splitlines() if line.strip()]
   finals=[row["result"] for row in events if row.get("event")=="result"]
   steps={row.get("step_update",{}).get("step_type") for row in events if row.get("event")=="step_update"}
   if not finals or finals[-1].get("status")!="SUCCESS" or not finals[-1].get("response","").strip():
    raise SystemExit("No successful nonempty review response; inspect local output")
   if steps-{"user_input","agent_response"}:
    raise SystemExit("Unexpected tool/action steps; inspect before accepting review: "+repr(steps))
   (run/"response.md").write_bytes(finals[-1]["response"].encode("utf-8"))
  print("Exit:",result.returncode,"Output:",run)
  raise SystemExit(result.returncode)
 except subprocess.TimeoutExpired:raise SystemExit("Timed out after 240s; inspect local output before retrying")
