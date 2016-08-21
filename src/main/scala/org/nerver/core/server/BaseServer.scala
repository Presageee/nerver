package org.nerver.core.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import org.nerver.core.HandlerScanner
import org.nerver.core.annotation.Method
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
      val serverBootstrap = new ServerBootstrap()
      serverBootstrap.option[java.lang.Integer](ChannelOption.SO_BACKLOG, 1024)
      serverBootstrap.childOption[java.lang.Boolean](ChannelOption.TCP_NODELAY, true)
      serverBootstrap.childOption[java.lang.Boolean](ChannelOption.SO_KEEPALIVE, true)

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
  val getMethodMap: scala.collection.mutable.HashMap[String, java.lang.reflect.Method] = scala.collection.mutable.HashMap()
  val postMethodMap: scala.collection.mutable.HashMap[String, java.lang.reflect.Method] = scala.collection.mutable.HashMap()
  val getHandlerMap: scala.collection.mutable.HashMap[String, Any] = scala.collection.mutable.HashMap()
  val postHandlerMap: scala.collection.mutable.HashMap[String, Any] = scala.collection.mutable.HashMap()
  val typeMap = scala.collection.mutable.HashMap(Method.GET -> getHandlerMap, Method.POST -> postHandlerMap)
  val methodTypeMap = scala.collection.mutable.HashMap(Method.GET -> getMethodMap, Method.POST -> postMethodMap)

  def main(args: Array[String]): Unit = {
    val url = Thread.currentThread().getContextClassLoader().getResource("org/nerver/core/annotation")
    val str = url.getPath()
    val handlerScanner = new HandlerScanner(args(1))
    handlerScanner.init()
    val baseServer = new BaseServer(Integer.parseInt(args(0)))
    baseServer.init()
  }
}
