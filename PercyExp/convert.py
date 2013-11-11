#coding:utf-8

import json

# 得到结果

def initCountT(location):
    T = dict()
    count = 0
    with open(location) as f:
        for line in f:
            s = float(line.strip())
            T[count] = s
            count += 1
    return T

def initDict(location):
    V = dict()
    with open(location) as f:
        for line in f:
            v,i = json.loads(line)
            V[i] = v
    return V

def process1(V,location1,location2):
    f1 = open(location1,mode='r')
    f2 = open(location2,mode='w')
    
    count = 0
    for line in f1:
        v = V[count]
        ns = line.strip().split()
        us = list()
        for s in ns:
            n,c = s.split(':')
            n = int(n)
            c = float(c)
            #c = T[n]*c
            us.append((n,c))
        line = json.dumps((v,us),ensure_ascii=False)
        f2.write(line.encode('utf-8')+'\n')
        count += 1
        if count % 1000 == 0:
            print count
    
    f1.close()
    f2.close()
    
def process2(location1,location2):
    T = dict()
    
    with open(location1) as f:
        for line in f:
            v,us = json.loads(line)
            for n,c in us:
                try: T[n]
                except: T[n] = list()
                T[n].append((v,c))
    
    print len(T)
    
    with open(location2,mode='w') as f:
        Ts = [ (t,vs) for t,vs in T.items()]
        Ts.sort(key = lambda x : x[0])
        for t,vs in Ts:
            vs.sort(key = lambda x : -x[1])
            line = json.dumps((t,vs),ensure_ascii=False)
            f.write(line.encode('utf-8')+'\n')

def process3(location1,location2,location3):
    countT = dict()
    countV = dict()
    with open(location1) as f:
        for line in f:
            v,us = json.loads(line)
            m = dict()
            for n,c in us:
                n = str(n)
                try: countT[n]
                except: countT[n] = 0
                countT[n] += c
                m[n] = c
            countV[v] = m
    
    print len(countT),len(countV)
    with open(location2,mode='w') as f:
        for n,c in countT.items():
            line = json.dumps((n,c),ensure_ascii=False)
            f.write(line.encode('utf-8')+'\n')
    
    with open(location3,mode='w') as f:
        for v,m in countV.items():
            if len(m) == 0:
                continue
            line = json.dumps((v,m),ensure_ascii=False)
            f.write(line.encode('utf-8')+'\n')
                
            

if __name__ == '__main__':
    
    print 'Convert'
    
    #T = initCountT('countT')
    #V = initDict('ap.dict.txt') 
    #process1(V,'countV','countV.dat')
    
    process2('countV.dat','countV.invert.dat')
    
    #process3('countV.dat','countT.txt','countV.txt')