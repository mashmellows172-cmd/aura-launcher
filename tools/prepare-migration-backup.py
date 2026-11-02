"""Add explicitly observed unlock settings to a private beta49 export."""
import argparse
import base64
import hashlib
import json
from pathlib import Path

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument("source", type=Path)
parser.add_argument("destination", type=Path)
parser.add_argument("--style", required=True, choices=["AUTO", "NONE", "SLIDE", "CRT", "FOLD", "ORBITAL", "RIPPLE", "RIBBON"])
parser.add_argument("--duration", required=True, type=int)
parser.add_argument("--intensity", required=True, type=int)
parser.add_argument("--light", required=True, choices=["true", "false"])
args = parser.parse_args()
if args.destination.exists():
    raise SystemExit("Destination already exists")
data = json.loads(args.source.read_text(encoding="utf-8"))
if data["format"] != "aura-settings" or data["version"] != 13:
    raise SystemExit("Expected beta49 format13 export")
if not 200 <= args.duration <= 650 or not 0 <= args.intensity <= 100:
    raise SystemExit("Invalid motion parameters")
for key, encoded in data["assets"].items():
    image = base64.b64decode(encoded, validate=True)
    if not image.startswith(b"\x89PNG\r\n\x1a\n") or data["settings"].get(key) != "asset:" + key:
        raise SystemExit("Invalid image/reference")
data["version"] = 14
data["settings"].update(unlock_style=args.style, unlock_duration=args.duration,
                        unlock_intensity=args.intensity, performance_light=args.light == "true")
payload = json.dumps(data, ensure_ascii=False, separators=(",", ":")).encode("utf-8")
with args.destination.open("xb") as output:
    output.write(payload)
print(json.dumps({"bytes": len(payload), "images": len(data["assets"]),
                  "sha256": hashlib.sha256(payload).hexdigest()}))
