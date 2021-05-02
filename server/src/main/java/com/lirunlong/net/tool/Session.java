package com.lirunlong.net.tool;

import java.util.HashMap;
import java.util.Map;

public class Session {
    public String sid;
    public Map<String, Object> sessionMap =  new HashMap<>();
    public long lastActiveTime ;
    public Session(String sid){
        this.sid = sid;
        active();
    }
    public void active(){
        lastActiveTime = System.currentTimeMillis();
    }
}
