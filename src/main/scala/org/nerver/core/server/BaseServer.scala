package org.nerver.core.server

import com.sun.net.httpserver.HttpServer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelOption, EventLoopGroup}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpResponseDecoder
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import org.nerver.core.handler.HttpServerHandlerInitializer

/**
  * Created by LJT on 2016/8/11.
  */
class BaseServer(p: Integer) {
  val port = p

  /**
    * init server
    */
  def init(): Unit = {
    //configure server
    val bossGroup = new NioEventLoopGroup()
    val workGroup = new NioEventLoopGroup()
    try {
      val serverBootstrap = new ServerBootstrap();
      serverBootstrap.option[java.lang.Integer](ChannelOption.SO_BACKLOG, 1024)
      //serverBootstrap.option(ChannelOption.TCP_NODELAY, true)
      //serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true)

      serverBootstrap.group(bossGroup, workGroup)
          .channel(classOf[NioServerSocketChannel])
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new HttpServerHandlerInitializer())
      // TODO: add http handler
      val channel = serverBootstrap.bind(port).sync().channel()
      channel.closeFuture().sync()
    } finally {
      bossGroup.shutdownGracefully()
      workGroup.shutdownGracefully()
    }
  }
}

object BaseServer {
  def main(args: Array[String]): Unit = {
    val baseServer = new BaseServer(8080)
    baseServer.init()
  }
}
