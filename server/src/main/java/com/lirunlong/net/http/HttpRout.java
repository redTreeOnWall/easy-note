package com.lirunlong.net.http;

import java.util.HashMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

public class HttpRout{
    public HashMap<String,HttpRoutHandle> routs =  new HashMap<>();
    public int addRout(String uri,HttpRoutHandle handle){
        if(routs.containsKey(uri)){
            return 0;
        }else{
            routs.put(uri, handle);
            return 1;
        }
    }
    public void rout(ChannelHandlerContext ctx, HttpRequest request, HttpContent content) {
        String uri = request.uri();
        String[] p =  uri.split("\\?");
        // System.out.println("get rui:"+uri);
        if(routs.containsKey(p[0])){
            routs.get(p[0]).onRout(ctx, request, content);
        }else{
            HttpHandle.responsString(ctx, "404", request);
        }
    }
}