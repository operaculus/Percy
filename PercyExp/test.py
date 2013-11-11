
from pymongo import Connection

_conn = Connection('10.127.10.27')

_conn = Connection('10.127.10.27')
Data = _conn.news.data

import json
import datetime

import urllib

import re

p = re.compile(r'[a-z]')

def init_text(location):
    T1 = datetime.datetime(2013,5,1)
    T2 = datetime.datetime(2013,5,2)
    
    T1 = T1.strftime("%Y-%m-%d %H:%M:%S")
    T2 = T2.strftime("%Y-%m-%d %H:%M:%S")
    
    cur = Data.find({'insert_time':{"$gte":T1,"$lt":T2}},\
                    {'seg_content':1,'topics':1})
    
    with open(location,mode='w') as f:
        count = 0
        
        for n in cur:

            if len(n['seg_content']) == 0:
                continue
            if p.match(n['seg_content'][0]) != None:
                continue
            text = json.dumps(n,ensure_ascii=False)
            f.write(text.encode('utf-8')+'\n')
            
            count += 1
            if count % 1000 == 0:
                print count
            if count > 5000:
                break
            
def test1(location1,location2):
    
    f1 = open(location1,mode='r')
    f2 = open(location2,mode='w')
    
    count = 0
    
    for line in f1:
        count += 1
        if count % 10 == 0:
            print count
            
        n = json.loads(line)
        
        vs = n['seg_content'].split()
        text = ','.join(vs)
        data = {"text":text.encode('utf-8')}
        data = urllib.urlencode(data)
        rp = urllib.urlopen("http://0.0.0.0:6376/test",data)
        
        res = rp.read()
        res = json.loads(res)
        
        rs = [ (z,s) for z,s in res.items() if s > 0.1]
        rs.sort(key = lambda r : -r[1])
        
        n['zs'] = rs
        
        del n['seg_content']
        
        line = json.dumps(n,ensure_ascii=False)
        f2.write(line.encode('utf-8')+'\n')
    
    f1.close()
    f2.close()   
    
def test2():
    
    u = dict()
    #u['keyword_list'] = [('林书豪',33.3)]
    u['seg_content'] = '中国 中国 林书豪 姚明 美女'
    data = json.dumps(u,ensure_ascii=False)
    #l = [data]
    #line = '\n'.join(l)
    rp = urllib.urlopen('http://0.0.0.0:6376/handle',data)
    ts = rp.read()
    #print json.dumps(ts,ensure_ascii=False)
    print json.loads(ts)
    
def test3():

    vs = ['中国', '中国', '林书豪', '姚明', '美女']
    vs = json.dumps(vs,ensure_ascii=False)
    vs = urllib.urlencode({'vs':vs})
    rp = urllib.urlopen('http://0.0.0.0:6376/', vs)
    ts = rp.read()
    ts = json.loads(ts)
    print json.dumps(ts,ensure_ascii=False)    
             
if __name__ == '__main__':
    
    print 'Count'
    
    #init_text("test.dat")
    
    #test1("test.dat","res.dat")
    
    test2()
    test3()