package org.nerver.core

import java.util
import java.util.jar.{JarEntry, JarFile}

import org.nerver.core.annotation.{Handler, Mapping}
import org.nerver.core.server.BaseServer
import org.nerver.core.utils.PathUtil

import scala.util.Try

/**
  * Created by LJT on 2016/8/17.
  */
class HandlerScanner(pkg: String) {
  val url = getClass.getResource("")

  def init(): Unit = {
    val path = PathUtil.getPath(url.getPath)
    loadJar(path)
  }

  def loadJar(path: String): Unit = {
    val jarFile = new JarFile(path)

    val entries: util.Enumeration[JarEntry] = jarFile.entries()
    while (entries.hasMoreElements) {
      val entry = entries.nextElement()
      val name = entry.getName
      if (name.contains(pkg)) {
        println(name)
        //auto scanner handler
        Try{
          val className = name.substring(0, name.lastIndexOf(".")).replaceAll("/", ".")
          val clazz = Class.forName(className)
          val an = clazz.getAnnotation(classOf[Handler])
          if (null != an) {
            //val obj = clazz.newInstance()
            val methods = clazz.getDeclaredMethods
            methods.foreach(m => {
              Try{
                val methodAnnotation = m.getAnnotation(classOf[Mapping])
                val route = methodAnnotation.value()
                val methodType = methodAnnotation.method()
                BaseServer.typeMap(methodType) += (route -> clazz)
                BaseServer.methodTypeMap(methodType) += (route -> m)
              }
            })
          }
        }
      }
    }
  }
}
