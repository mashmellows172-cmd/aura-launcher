"""Read the Preferences protobuf captured from our debuggable QA build, without extra dependencies."""
def fields(data):
    def number(i):
        n=0;shift=0
        while True:
            b=data[i];i+=1;n|=(b&127)<<shift
            if b<128:return n,i
            shift+=7
    i=0
    while i<len(data):
        tag,i=number(i);wire=tag&7
        if wire==2:
            size,i=number(i);value=data[i:i+size];i+=size
        elif wire==0:value,i=number(i)
        elif wire in (1,5):
            size=8 if wire==1 else 4;value=data[i:i+size];i+=size
        else:raise ValueError(wire)
        yield tag>>3,value

def read_preferences(path):
    result={}
    for _,entry in fields(path.read_bytes()):
        parts=dict(fields(entry));key=parts[1].decode();value=list(fields(parts[2]))
        result[key]=value[0][1].decode() if len(value)==1 and value[0][0]==5 else repr(value)
    return result
