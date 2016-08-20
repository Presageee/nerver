package org.nerver.core.handler

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{HttpObjectAggregator, HttpRequestDecoder, HttpServerCodec}

/**
  * Created by LJT on 2016/8/12.
  */
class HttpServerHandlerInitializer extends ChannelInitializer[SocketChannel]{
  override def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline()

    //add handler
    pipeline.addLast(new HttpServerCodec())
    pipeline.addLast(new HttpObjectAggregator(1048576))
    pipeline.addLast(new HttpHandler())

  }
}
