package com.lirunlong.net.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public abstract class HttpHandle extends ChannelInboundHandlerAdapter { // 1

    private HttpRequest request;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;

        }
        if (msg instanceof HttpContent) {
            serve(ctx,request,(HttpContent)msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    public abstract void serve(ChannelHandlerContext ctx,HttpRequest request,HttpContent content);

    public static void responsString(ChannelHandlerContext ctx,String res,HttpRequest request){

        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set("Access-Control-Allow-Origin", "*");
        response.headers().set(CONTENT_LENGTH,
                response.content().readableBytes());
        if (HttpHeaders.isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);
        ctx.flush();
        ctx.close();
    }
}


