package com.lirunlong.net.http;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;

import com.lirunlong.thread.ThreadPool;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;

import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class HttpServer {
    public class ConstVal {
        public static final String COOKIE = "Cookie";
    }

    public Map sessionMap;

    public interface GetHttpHandle {
        public HttpHandle HttpHandle();
    }

    private final int port;
    public HttpRout rout;
    public ThreadPool threadPool;

    public HttpServer(int port) {
        this.port = port;
        threadPool = new ThreadPool(100, 10000);
        sessionMap = new Hashtable<>();
    }

    public HttpServer addRout(String uri, HttpRoutHandle h) {
        if (rout == null) {
            rout = new HttpRout();
        }
        int code = rout.addRout(uri, h);
        if (code == 0) {
            System.out.println("handleErr:" + uri);
        }
        return this;
    }

    public void start(GetHttpHandle getHandle) {

        final SslContext sslCtx;
        File file = new File("/root/leefile/key/2941847_lirunlong.com_other/2941847_lirunlong.com.pem");
        File key = new File("/root/leefile/key/2941847_lirunlong.com_tomcat/server.key");
        try {
            sslCtx = SslContextBuilder.forServer(file, key).build();
        } catch (SSLException e) {
            e.printStackTrace();
            return;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        // NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline();
                        if (sslCtx != null) {
                            ch.pipeline().addLast("ssl", sslCtx.newHandler(ch.alloc()));
                        }
                        ch.pipeline().addLast("decoder", new HttpRequestDecoder()) // 1
                                .addLast("encoder", new HttpResponseEncoder()) // 2
                                // .addLast("aggregator", new HttpObjectAggregator(512 * 1024)) // 3
                                .addLast("handler", getHandle.HttpHandle()); // 4
                                // .addLast("handler",new HttpSnoopServerHandler()); // 4
                                // System.out.println("new handler!");
                    }
                }).option(ChannelOption.SO_BACKLOG, 10240);
        // }).option(ChannelOption.SO_BACKLOG, 10240) // determining the number of
        // connections queued
        // .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        try {
            ChannelFuture future = b.bind(port);
            future.addListener(f -> {
                if (future.isSuccess()) {
                    System.out.println("Http Server active on port " + port);
                } else {
                    System.out.println("http server faild;");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class HttpSnoopServerHandler extends SimpleChannelInboundHandler<Object> {

        private HttpRequest request;
        /** Buffer that stores the response content */
        private final StringBuilder buf = new StringBuilder();

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof HttpRequest) {
                HttpRequest request = this.request = (HttpRequest) msg;

                if (HttpUtil.is100ContinueExpected(request)) {
                    send100Continue(ctx);
                }

                buf.setLength(0);
                buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
                buf.append("===================================\r\n");

                buf.append("VERSION: ").append(request.protocolVersion()).append("\r\n");
                buf.append("HOSTNAME: ").append(request.headers().get(HttpHeaderNames.HOST, "unknown")).append("\r\n");
                buf.append("REQUEST_URI: ").append(request.uri()).append("\r\n\r\n");

                HttpHeaders headers = request.headers();
                if (!headers.isEmpty()) {
                    for (Map.Entry<String, String> h : headers) {
                        CharSequence key = h.getKey();
                        CharSequence value = h.getValue();
                        buf.append("HEADER: ").append(key).append(" = ").append(value).append("\r\n");
                    }
                    buf.append("\r\n");
                }

                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
                Map<String, List<String>> params = queryStringDecoder.parameters();
                if (!params.isEmpty()) {
                    for (var p : params.entrySet()) {
                        String key = p.getKey();
                        List<String> vals = p.getValue();
                        for (String val : vals) {
                            buf.append("PARAM: ").append(key).append(" = ").append(val).append("\r\n");
                        }
                    }
                    buf.append("\r\n");
                }

                appendDecoderResult(buf, request);
            }

            if (msg instanceof HttpContent) {
                HttpContent httpContent = (HttpContent) msg;

                ByteBuf content = httpContent.content();
                if (content.isReadable()) {
                    buf.append("CONTENT: ");
                    buf.append(content.toString(CharsetUtil.UTF_8));
                    buf.append("\r\n");
                    appendDecoderResult(buf, request);
                }

                if (msg instanceof LastHttpContent) {
                    buf.append("END OF CONTENT\r\n");

                    LastHttpContent trailer = (LastHttpContent) msg;
                    if (!trailer.trailingHeaders().isEmpty()) {
                        buf.append("\r\n");
                        for (CharSequence name : trailer.trailingHeaders().names()) {
                            for (CharSequence value : trailer.trailingHeaders().getAll(name)) {
                                buf.append("TRAILING HEADER: ");
                                buf.append(name).append(" = ").append(value).append("\r\n");
                            }
                        }
                        buf.append("\r\n");
                    }

                    if (!writeResponse(trailer, ctx)) {
                        // If keep-alive is off, close the connection once the content is fully written.
                        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                    }
                }
            }
        }

        private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
            DecoderResult result = o.decoderResult();
            if (result.isSuccess()) {
                return;
            }

            buf.append(".. WITH DECODER FAILURE: ");
            buf.append(result.cause());
            buf.append("\r\n");
        }

        private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
            // Decide whether to close the connection or not.
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            // Build the response object.
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                    currentObj.decoderResult().isSuccess() ? OK : BAD_REQUEST,
                    Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

            if (keepAlive) {
                // Add 'Content-Length' header only for a keep-alive connection.
                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                // Add keep alive header as per:
                // -
                // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            // Encode the cookie.
            String cookieString = request.headers().get(HttpHeaderNames.COOKIE);
            if (cookieString != null) {
                Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
                if (!cookies.isEmpty()) {
                    // Reset the cookies if necessary.
                    for (Cookie cookie : cookies) {
                        response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
                    }
                }
            } else {
                // Browser sent no cookie. Add some.
                response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key1", "value1"));
                response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key2", "value2"));
            }

            // Write the response.
            ctx.write(response);

            return keepAlive;
        }

        private static void send100Continue(ChannelHandlerContext ctx) {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER);
            ctx.write(response);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
