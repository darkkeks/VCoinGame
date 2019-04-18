package ru.darkkeks.vcoin.game.callback;

import com.vk.api.sdk.callback.CallbackApi;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Values.CLOSE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.apache.http.HttpHeaders.CONNECTION;

public class CallbackHandler extends SimpleChannelInboundHandler<HttpObject> {

    private CallbackApi api;
    private ByteBuf ok;

    public CallbackHandler(CallbackApi api) {
        this.api = api;
        ok = Unpooled.copiedBuffer("ok".getBytes());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if(msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;

            String content = req.content().toString(Charset.forName("UTF-8"));
            if(!content.isEmpty()) {
                api.parse(content);
            }

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, ok);
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
