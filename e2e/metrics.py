import argparse,json,re,time
from device import Device,EVIDENCE
p=argparse.ArgumentParser();p.add_argument('--address',required=True);p.add_argument('--label',default='metrics');a=p.parse_args()
d=Device(a.address);pkg='dev.senda.launcher.debug'
report=EVIDENCE/a.label;report.mkdir(exist_ok=True)
since=d.shell('date','+%m-%dT%H:%M:%S.000').strip().replace('T',' ')
d.shell('am','start','-a','android.settings.SETTINGS')
d.shell('am','force-stop',pkg)
start=d.shell('am','start','-W','-n',pkg+'/dev.senda.launcher.MainActivity','-a','android.intent.action.MAIN','-c','android.intent.category.HOME')
(report/'startup.txt').write_text(start,encoding='utf-8')
time.sleep(1)
d.shell('dumpsys','gfxinfo',pkg,'reset')
for _ in range(4):
    d.shell('input','swipe','550','2300','550','850','300')
    d.shell('input','swipe','550','850','550','2300','300')
gfx=d.shell('dumpsys','gfxinfo',pkg)
memory=d.shell('dumpsys','meminfo',pkg)
display=d.shell('dumpsys','display')
crashes=d.run('-s',a.address,'logcat','-d','-T',since,'-s','AndroidRuntime:E')
for name,value in [('gfxinfo',gfx),('meminfo',memory),('display',display),('runtime',crashes)]:
    (report/(name+'.txt')).write_text(value,encoding='utf-8')
result={'launch_state':next((line.strip() for line in start.splitlines() if 'LaunchState:' in line),'unknown')}
for name,pattern,text in [
    ('startup_total_ms',r'TotalTime:\s*(\d+)',start),
    ('total_frames',r'Total frames rendered:\s*(\d+)',gfx),
    ('janky_frames',r'Janky frames:\s*(\d+)',gfx),
    ('total_pss_kb',r'TOTAL PSS:\s*(\d+)',memory)]:
    match=re.search(pattern,text)
    if match:result[name]=int(match.group(1))
result['crash_log_mentions_aura']=('Process: '+pkg) in crashes
result['scope']='One cold process launch and eight swipes. Not an all-day battery benchmark; ambient load differs between runs.'
(report/'metrics.json').write_text(json.dumps(result,indent=2),encoding='utf-8')
d.shell('input','keyevent','KEYCODE_HOME')
print(json.dumps(result,indent=2))
