package org.nerver.core.utils

/**
  * Created by LJT on 2016/8/17.
  */
class PathUtil {


}
object PathUtil {
  def getPath(url: String):String = {
    val start = url.indexOf(":")
    val end = url.lastIndexOf("!")
    url.substring(start + 1, end)
  }
  def main(args: Array[String]): Unit = {
    //val p = new PathUtil()
    println(getPath("file:/H:/project/never/out/artifacts/never_jar/never.jar!/org/nerver/core/annotation"))
    println(getClass.getResource("../"))
  }
}
