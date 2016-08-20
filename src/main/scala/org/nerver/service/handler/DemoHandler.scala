package org.nerver.service.handler

import java.util

import org.nerver.core.annotation.{Handler, Mapping, Method}

import scala.collection.JavaConverters._

/**
  * Created by LJT on 2016/8/21.
  */
@Handler
class DemoHandler {

  @Mapping(value = "/hello", method = Method.GET)
  def helloWorld(map: util.HashMap[String, Any]): String = {
    "helloWorld"
  }

  @Mapping(value = "/get/decode/test", method = Method.GET)
  def getUrlEncodeTest(map: util.HashMap[String, Any]): String = {
    val sMap = map.entrySet().asScala
    val sb = new StringBuffer()
    sMap.foreach(e => sb.append(e.getKey + "-" + e.getValue + ";"))
    sb.toString
  }

  @Mapping(value = "/post/test", method = Method.POST)
  def postTest(map: util.HashMap[String, Any]): String = {
    val sMap = map.entrySet().asScala
    val sb = new StringBuffer()
    sMap.foreach(e => sb.append(e.getKey + "-" + e.getValue + ";"))
    sb.toString
  }
}
