package com.lirunlong.net.http;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

public interface HttpRoutHandle{
    public void onRout(ChannelHandlerContext ctx,HttpRequest request,HttpContent content);
}