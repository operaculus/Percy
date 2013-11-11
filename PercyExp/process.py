#coding:utf-8

import json

# 得到训练数据

# 得到基本的数据 [DocID, [Token] ]
def process1(filename1,filename2):
    f1 = open(filename1,mode='r')
    f2 = open(filename2,mode='w')
    
    ns = list()
    us = list()
    count = 0
    for line in f1:
        count += 1
        if count == 2:
            ns.append(line.strip()[7:-8].strip())
        if count == 4:
            us.append(line.strip())
        if count == 6:
            count = 0
    
    for i in range(len(ns)):
        line1 = ns[i]
        line2 = us[i]
        line = json.dumps((line1,line2),ensure_ascii=False)
        f2.write(line.encode('utf-8')+'\n')
    f1.close()
    f2.close()

# 统计词的DF   
def process2(filename1, filename2):
    f1 = open(filename1,mode='r')
    f2 = open(filename2,mode='w')
    
    M = dict()
    for line in f1:
        _,vs = json.loads(line)
        vs = line.strip().split()
        for v in vs:
            try: M[v]
            except: M[v] = 0
            M[v] += 1
    
    ms = [ (v,c) for v,c in M.items()]
    ms.sort(key = lambda x : -x[1])
    
    for v,c in ms:
        line = json.dumps((v,c),ensure_ascii=False)
        f2.write(line.encode('utf-8')+'\n')
    
    f1.close()
    f2.close()
    
def initM(location):
    M = dict()
    with open(location) as f:
        for line in f:
            v,c = json.loads(line)
            M[v] = c
    return M

def initV(location):
    M = dict()
    with open(location) as f:
        for line in f:
            v = line.strip()
            M[v] = 100
    return M

def saveN(N, location):
    ns = [ (v,n) for v,n in N.items()]
    ns.sort(key = lambda x : x[1])
    with open(location,mode='w') as f:
        for v,n in ns:
            line = json.dumps((v,n),ensure_ascii=False)
            f.write(line.encode('utf-8')+'\n')
            
# 转换数据
def process3(M, filename1, filename2):
    f1 = open(filename1,mode='r')
    f2 = open(filename2,mode='w')
    N = dict()
    count = 0
    for line in f1:
        count += 1
        name,vs = json.loads(line)
        vs = vs.split()
        us = list()
        for v in vs:
            if len(v) == 1: continue
            try: 
                if M[v] < 10: continue
            except: continue
            try: N[v]
            except: N[v] = len(N)
            u = str(N[v])
            us.append(u)
        
        if len(us) == 0:
            continue
            
        m = dict()
        for u in us:
            try: m[u]
            except: m[u] = 0
            m[u] += 1
        ms = [u+':'+str(c) for u,c in m.items()]
        line = name+'\t'+','.join(ms)
        f2.write(line.encode('utf-8')+'\n')
    f1.close()
    f2.close()
    return N,count
    
if __name__ == '__main__':
    
    print 'Hello, AP'
    
    #process1('ap/ap.txt', 'ap.raw.txt')
    #process2('ap.raw.txt', 'ap.raw.count.txt')
    #M = initM('ap.raw.count.txt')
    M = initV('ap/vocab.txt')
    print len(M)
    N,count = process3(M,'ap.raw.txt','ap.raw.convert.txt')
    saveN(N,'ap.dict.txt')
    with open('ap.size.txt',mode='w') as f:
        line = str(count)+','+str(len(N))
        f.write(line)
    
