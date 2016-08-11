package org.nerver.core.handler

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{HttpRequestDecoder, HttpServerCodec}

/**
  * Created by LJT on 2016/8/12.
  */
class HttpServerHandlerInitializer extends ChannelInitializer[SocketChannel]{
  override def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline()

    //add handler
    pipeline.addLast(new HttpServerCodec())

    pipeline.addLast(new HelloWorldHandler())

  }
}
