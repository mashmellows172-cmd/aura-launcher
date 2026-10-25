"""Read launcher identities from the authorized Motorola; never changes phone preferences.

The inventory is private. Labels are intentionally unresolved until verified from app metadata.
Run from the repository: python tools/capture-launcher-inventory.py --device IP:PORT
"""
import argparse
from datetime import datetime, timezone
import hashlib
import json
from pathlib import Path
import re
import subprocess

ROOT = Path(__file__).resolve().parents[1]
AUTHORIZED_SERIAL = "ZY22K88ZFL"
COMPONENT = re.compile(r"^[A-Za-z0-9_]+(?:\.[A-Za-z0-9_]+)+/[A-Za-z0-9_.$]+$")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--device", required=True, help="An already paired ADB device endpoint or USB serial")
    parser.add_argument("--adb", type=Path, default=ROOT.parents[1] / "work/android-toolchain/android-sdk/platform-tools/adb.exe")
    parser.add_argument("--output", type=Path, default=ROOT.parents[1] / "work/icon-inventory/motorola-launcher-inventory.json")
    args = parser.parse_args()

    def shell(*arguments):
        result = subprocess.run([str(args.adb), "-s", args.device, "shell", *arguments],
                                capture_output=True, text=True, encoding="utf-8", errors="replace", timeout=25)
        if result.returncode:
            raise RuntimeError(result.stderr.strip() or result.stdout.strip())
        return result.stdout.strip()

    serial = shell("getprop", "ro.serialno")
    if serial != AUTHORIZED_SERIAL:
        raise RuntimeError("Connected device is not the authorized Motorola; no inventory saved")
    user = shell("am", "get-current-user")
    if not user.isdecimal():
        raise RuntimeError("Cannot identify the active Android user")
    raw = shell("cmd", "package", "query-activities", "--brief", "--user", user,
                "-a", "android.intent.action.MAIN", "-c", "android.intent.category.LAUNCHER")
    components = sorted({line.strip() for line in raw.splitlines() if COMPONENT.fullmatch(line.strip())})
    if not components:
        raise RuntimeError("No verified launcher components returned; existing inventory preserved")
    entries = [{"package": component.split("/", 1)[0], "component": component,
                "label": None, "labelStatus": "unresolved", "artworkStatus": "pending"} for component in components]
    result = {"capturedUtc": datetime.now(timezone.utc).isoformat(), "serial": serial,
              "model": shell("getprop", "ro.product.model"), "androidUser": int(user),
              "source": "cmd package query-activities MAIN/LAUNCHER", "componentCount": len(entries),
              "packageCount": len({entry["package"] for entry in entries}),
              "rawSha256": hashlib.sha256(raw.encode()).hexdigest(), "entries": entries}
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    args.output.with_suffix(".adb.txt").write_text(raw + "\n", encoding="utf-8")
    print(f"Saved {result['componentCount']} launchable components / {result['packageCount']} packages to private inventory")
    print("Labels and six-family artwork coverage remain pending verification")


if __name__ == "__main__":
    main()
