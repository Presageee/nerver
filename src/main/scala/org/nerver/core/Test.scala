package org.nerver.core

import java.lang.annotation.Annotation

import org.nerver.core.annotation.Mapping

import scala.reflect.runtime.{universe => ru}
/**
  * Created by LJT on 2016/8/14.
  */
object Test {

  def main(args: Array[String]): Unit = {
    //val c = System.currentTimeMillis()
/*    val t = ru.runtimeMirror(getClass.getClassLoader)
    val classe = ru.typeOf[TestReflect].typeSymbol.asClass
    val cm = t.reflectClass(classe)
    val ctor = ru.typeOf[TestReflect].declaration(ru.nme.CONSTRUCTOR).asMethod
    val ctorm = cm.reflectConstructor(ctor)
    val res = ru.typeOf[TestReflect].declaration(ru.newTermName("print")).asMethod
    val vv = t.reflect(ctorm())
    val d = vv.reflectMethod(res)
    d.apply()*/
    //println(System.currentTimeMillis() - c)
    val cc = System.currentTimeMillis()
    val classes = Class.forName("org.nerver.core.TestReflect")
    val o = classes.newInstance()
    val me = classes.getMethod("print", classOf[String])
    val d = me.getDeclaredAnnotations()
    val dd = me.getAnnotation(classOf[Mapping]).method()
    println(dd)
    println(d.length)
    d.foreach(e => println(e.toString))
    for (i <- 0 until d.length) {
      println(d(i).annotationType())
    }
    me.invoke(o, "1111")
    println(System.currentTimeMillis() - cc)
  }
}
