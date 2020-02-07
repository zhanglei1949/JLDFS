package com.zhanglei.JLDFS;
public class MasterServer{
    int port;
    int ip;
    int replica;
    public MasterServer(int port, int ip, int replica){
        this.port = port;
        this.ip = ip;
        this.replica = replica;
    }
}