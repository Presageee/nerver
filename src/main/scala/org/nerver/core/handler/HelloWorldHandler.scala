package org.nerver.core.handler

import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.handler.codec.http.{DefaultFullHttpResponse, HttpRequest, HttpUtil}
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.handler.codec.http.HttpVersion._

/**
  * Created by LJT on 2016/8/12.
  */
class HelloWorldHandler extends ChannelInboundHandlerAdapter{
  val content = Array[Byte]('H'.toByte,'e'.toByte,'l'.toByte,'l'.toByte,'o'.toByte)
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    def write(request: HttpRequest): Unit = {
      if (HttpUtil.is100ContinueExpected(request)) {
        ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE))
      }
      val isKeepAlive = HttpUtil.isKeepAlive(request)
      val fullResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content))
      fullResponse.headers().set("Content-Type", "text/plain")
      fullResponse.headers().set("Content-Length", fullResponse.content().readableBytes())
      ctx.write(fullResponse)
    }
    msg match {
      case x: HttpRequest => write(x)
      case _ => println("error")
    }
    ctx.writeAndFlush("HelloWorld!")
    ctx.close()
  }


  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    println(">>> Exception: echo hello world")
    ctx.close()
  }
}
