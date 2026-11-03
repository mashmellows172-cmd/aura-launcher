"""Run one bounded local implementation proposal, keeping evidence outside Git."""
import argparse
import json
from pathlib import Path
import time
import urllib.request

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument("task", type=Path)
parser.add_argument("output", type=Path)
args = parser.parse_args()
task = json.loads(args.task.read_text(encoding="utf-8-sig"))
root = Path(task["repo"]).resolve()
sources = {}
for name in task.get("sources", []):
    path = (root / name).resolve()
    if not path.is_relative_to(root) or path.suffix not in {".kt", ".md"}:
        raise SystemExit("Source outside approved scope")
    sources[name] = path.read_text(encoding="utf-8")
editing = "Override output format" in task["instruction"]
shape = '{"edits":[{"path":"relative path","old":"exact source","new":"replacement"}],"notes":"brief limits"}' if editing else '{"files":[{"path":"relative path","content":"complete Kotlin source"}],"notes":"brief limits"}'
prompt = "Return JSON only. No reasoning, markdown, tools or commands. You implement Kotlin for Aura, Android Compose minSdk29. Preserve accessibility, user settings and lifecycle. Output " + shape + ".\n"
prompt += json.dumps({"instruction": task["instruction"], "source": sources}, ensure_ascii=False)
if len(prompt) > 22000:
    raise SystemExit("Task exceeds 22000-character context budget")
request = {"model": "deepseek-coder:6.7b", "raw": False, "prompt": prompt,
           "stream": False, "format": "json", "keep_alive": "1m",
           "options": {"temperature": 0, "repeat_penalty": 1.15, "num_ctx": 4096 if len(prompt) < 9000 else 8192,
                       "num_predict": task.get("budget", 1200)}}
fields = {"path": {"type": "string", "enum": task["outputs"]}}
fields.update({key: {"type": "string"} for key in (["old", "new"] if editing else ["content"])})
request["format"] = {"type": "object", "properties": {
    "edits" if editing else "files": {"type": "array", "minItems": 1, "items": {
        "type": "object", "properties": fields, "required": list(fields), "additionalProperties": False}},
    "notes": {"type": "string"}}, "required": ["edits" if editing else "files", "notes"], "additionalProperties": False}
started = time.monotonic()
req = urllib.request.Request("http://127.0.0.1:11434/api/generate", data=json.dumps(request).encode(),
                             headers={"Content-Type": "application/json"})
with urllib.request.urlopen(req, timeout=300) as response:
    reply = json.load(response)
args.output.mkdir(parents=True, exist_ok=False)
(args.output / "reply.json").write_text(json.dumps(reply, ensure_ascii=False, indent=2), encoding="utf-8")
if not reply.get("done") or reply.get("done_reason") == "length":
    raise SystemExit("Truncated response. Supervisor revision required")
proposal = json.loads(reply["response"])
kind = "edits" if editing else "files"
if not isinstance(proposal, dict) or not isinstance(proposal.get(kind), list) or not all(isinstance(item, dict) for item in proposal[kind]):
    raise SystemExit("Invalid proposal structure")
if not proposal.get("files") and not proposal.get("edits"):
    raise SystemExit("No implementation supplied")
for item in proposal.get("files", []):
    if item["path"] not in task["outputs"] or not isinstance(item["content"], str):
        raise SystemExit("Output outside task contract")
for item in proposal.get("edits", []):
    if item["path"] not in task["outputs"] or not item.get("old") or not isinstance(item.get("new"), str):
        raise SystemExit("Edit outside task contract")
    if sources[item["path"]].count(item["old"]) != 1:
        raise SystemExit("Edit does not match exactly once")
(args.output / "proposal.json").write_text(json.dumps(proposal, ensure_ascii=False, indent=2), encoding="utf-8")
print(json.dumps({"seconds": round(time.monotonic()-started, 2), "tokens": reply.get("eval_count"),
                  "output": str(args.output), "paths": [item.get("path") for item in proposal[kind]],
                  "notes": proposal.get("notes", "")}, ensure_ascii=False))
