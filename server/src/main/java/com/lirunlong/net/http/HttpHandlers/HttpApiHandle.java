package com.lirunlong.net.http.HttpHandlers;
import com.lirunlong.main.MainClass;
import com.lirunlong.net.http.HttpHandle;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

public class HttpApiHandle extends HttpHandle {
    
    @Override
    public void serve(ChannelHandlerContext ctx, HttpRequest request, HttpContent content) {
        // var cookie =  request.headers().get("Cookie");
        // System.out.println(cookie);
        // if(cookie == null){
        //     var sid = 
        //     MainClass.getInstance().apiServer.sessionMap
        // }else{

        // }
        //TODO: seesion and cookie
        MainClass.getInstance().apiServer.rout.rout(ctx, request, content);
    }
}
