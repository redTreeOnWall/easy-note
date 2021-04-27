package com.lirunlong.httpApi;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

public interface IHttpHandle {
    public void serve(ChannelHandlerContext ctx, HttpRequest request, HttpContent content) ;
}