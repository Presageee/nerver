package org.nerver.core.handler

import java.util
import java.util.Map.Entry
import java.util.function.Consumer

import com.oracle.jrockit.jfr.ContentType
import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.handler.codec.http.HttpVersion._
import io.netty.util.{AsciiString, CharsetUtil}
import org.nerver.core.annotation.{Handler, Mapping, Method}
import org.nerver.core.exception.{BaseException, ContentTypeException, HTTPMethodNotSupportException, NotFoundException}
import org.nerver.core.server.BaseServer

/**
  * Created by LJT on 2016/8/12.
  */
class HttpHandler extends ChannelInboundHandlerAdapter{
  val content = Array[Byte]('H'.toByte,'e'.toByte,'l'.toByte,'l'.toByte,'o'.toByte)
  val CONNECTION = new AsciiString("Connection")
  val KEEP_ALIVE = new AsciiString("keep-alive")

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    //define get
    def get(request: HttpRequest, keepAlive: Boolean): Unit = {
      //decode
      val uri = request.getUri
      val queryDecoder = new QueryStringDecoder(uri)
      val route = queryDecoder.path()
      val map = new util.HashMap[String, Any]()
      val entry = queryDecoder.parameters().entrySet()
      import scala.collection.JavaConverters._
      val se = entry.asScala
      se.foreach(e => map.put(e.getKey, e.getValue.get(0)))
      val clazz = BaseServer.typeMap(Method.GET) (route)
      val instanceObj = clazz.newInstance()
      val invokeResult = BaseServer.getMethodMap(route).invoke(instanceObj, map).asInstanceOf[String]
      val fullResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(invokeResult.getBytes("UTF-8")))
      fullResponse.headers().set("Content-Type", "text/plain")
      fullResponse.headers().set("Content-Length", fullResponse.content().readableBytes())
      keepAlive match {
        case true => ctx.write(fullResponse).addListener(ChannelFutureListener.CLOSE)
        case _ =>
          fullResponse.headers().set("Connection", "keep-alive")
          ctx.write(fullResponse)

      }
    }

    //define post
    def post(request: HttpRequest, keepAlive: Boolean): Unit = {
      def urlEncode(request: HttpRequest, keepAlive: Boolean): Unit = {
        val uri = request.getUri
        val queryDecoder = new QueryStringDecoder(uri)
        val route = queryDecoder.path()
        val map = new util.HashMap[String, Any]()
        val entry = queryDecoder.parameters().entrySet()
        import scala.collection.JavaConverters._
        val se = entry.asScala
        se.foreach(e => map.put(e.getKey, e.getValue.get(0)))
        val clazz = BaseServer.typeMap(Method.POST) (route)
        val instanceObj = clazz.newInstance()//thread safe
        val invokeResult = BaseServer.postMethodMap(route).invoke(instanceObj, map).asInstanceOf[String]
        val fullResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(invokeResult.getBytes("UTF-8")))
        fullResponse.headers().set("Content-Type", "text/plain")
        fullResponse.headers().set("Content-Length", fullResponse.content().readableBytes())
        keepAlive match {
          case true => ctx.write(fullResponse).addListener(ChannelFutureListener.CLOSE)
          case _ =>
            fullResponse.headers().set("Connection", "keep-alive")
            ctx.write(fullResponse)
        }
      }

      def multipart(request: HttpRequest, keepAlive: Boolean): Unit = {
        val fullResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("this is multipart request".getBytes("UTF-8")))
        fullResponse.headers().set("Content-Type", "text/plain")
        fullResponse.headers().set("Content-Length", fullResponse.content().readableBytes())
        keepAlive match {
          case true => ctx.write(fullResponse).addListener(ChannelFutureListener.CLOSE)
          case _ =>
            fullResponse.headers().set("Connection", "keep-alive")
            ctx.write(fullResponse)
        }
      }

      def json(request: HttpRequest, keepAlive: Boolean): Unit = {
        val fullResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("this is json request".getBytes("UTF-8")))
        fullResponse.headers().set("Content-Type", "text/plain")
        fullResponse.headers().set("Content-Length", fullResponse.content().readableBytes())
        keepAlive match {
          case true => ctx.write(fullResponse).addListener(ChannelFutureListener.CLOSE)
          case _ =>
            fullResponse.headers().set("Connection", "keep-alive")
            ctx.write(fullResponse)
        }
      }
      val fr = request.asInstanceOf[FullHttpRequest]
      request.headers().get("Content-Type") match {
        case "application/x-www-form-urlencoded" => urlEncode(request, keepAlive)
        case "multipart/form-data" => multipart(request, keepAlive)
        case "application/json" => json(request, keepAlive)
        case _ => throw new ContentTypeException("can not solve this type")
      }
    }


    def write(request: HttpRequest): Unit = {
      if (HttpUtil.is100ContinueExpected(request)) {
        ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE))
      }
      val path = request.uri()
      def isExists = (e: String) => {
        val len = e.indexOf("?")
        if (len == -1) {
          e.length
        } else {
         len
        }
      }
      val trueRoute = path.substring(0, isExists(path))
      if (!BaseServer.methodTypeMap(Method.GET).contains(trueRoute) && !BaseServer.methodTypeMap(Method.POST).contains(trueRoute)) {
        throw new NotFoundException("UNKNOWN_ROUTE")
      }
      val isKeepAlive = HttpUtil.isKeepAlive(request)
      val method = request.method()
      method match {
        case HttpMethod.GET => get(request, isKeepAlive)
        case HttpMethod.POST => post(request, isKeepAlive)
        case _ => throw new HTTPMethodNotSupportException("cannot support this method:" + method)
      }
    }

    msg match {
      case x: HttpRequest => write(x)
      case _ => throw new BaseException("read error")
    }
  }


  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
    ctx.close()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    def returnByType(cause: Throwable):DefaultFullHttpResponse = cause match {
      case e: ContentTypeException =>
        val fullResponse = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST, Unpooled.wrappedBuffer(e.getMsg().getBytes("UTF-8")))
        fullResponse.headers().set("Content-Type", "text/plain")
        fullResponse.headers().set("Content-Length", fullResponse.content().readableBytes())
        fullResponse

      case f: HTTPMethodNotSupportException =>
        val fullResponse = new DefaultFullHttpResponse(HTTP_1_1, METHOD_NOT_ALLOWED, Unpooled.wrappedBuffer(f.getMsg().getBytes("UTF-8")))
        fullResponse.headers().set("Content-Type", "text/plain")
        fullResponse.headers().set("Content-Length", fullResponse.content().readableBytes())
        fullResponse

      case g: NotFoundException =>
        val fullResponse = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND, Unpooled.wrappedBuffer(g.getMsg().getBytes("UTF-8")))
        fullResponse.headers().set("Content-Type", "text/plain")
        fullResponse.headers().set("Content-Length", fullResponse.content().readableBytes())
        fullResponse

      case _ =>
        val fullResponse = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR, Unpooled.wrappedBuffer(cause.asInstanceOf[BaseException].getMsg().getBytes("UTF-8")))
        fullResponse.headers().set("Content-Type", "text/plain")
        fullResponse.headers().set("Content-Length", fullResponse.content().readableBytes())
        fullResponse

    }
    val response = returnByType(cause)
    ctx.write(response)
    ctx.flush()
    ctx.close()
  }
}

