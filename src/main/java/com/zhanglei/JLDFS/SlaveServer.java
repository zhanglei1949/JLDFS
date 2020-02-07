package com.zhanglei.JLDFS;
public class SlaveServer{
    int port;
    int ip;
    int replica;
    public SlaveServer(int port, int ip, int replica){
        this.port = port;
        this.ip = ip;
        this.replica = replica;
    }
}