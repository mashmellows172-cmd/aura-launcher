"""Package tracked project sources only; never include .git, builds, private QA or user assets."""
import argparse
from pathlib import Path, PurePosixPath
import subprocess
import zipfile

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output',type=Path,required=True)
    args=parser.parse_args()
    root=Path(__file__).resolve().parents[1]
    paths=subprocess.check_output(['git','-C',str(root),'ls-files','-z']).decode('utf-8').split('\0')
    output=args.output.resolve()
    output.parent.mkdir(parents=True,exist_ok=True)
    excluded={'beta','build','.gradle','.kotlin','.git','.superpowers','work','__pycache__'}
    with zipfile.ZipFile(output,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as archive:
        for name in sorted(filter(None,paths)):
            parts=PurePosixPath(name).parts
            path=root.joinpath(*parts)
            if any(part in excluded for part in parts) or path.suffix.lower() in {'.apk','.idsig','.zip','.jks','.keystore','.pyc'} or path.name=='local.properties':
                continue
            if path.is_file(): archive.write(path,'aura-launcher/'+name)
    with zipfile.ZipFile(output) as archive:
        assert archive.testzip() is None
        print(f'{output}: {len(archive.namelist())} source files, {output.stat().st_size} bytes')

if __name__=='__main__': main()
