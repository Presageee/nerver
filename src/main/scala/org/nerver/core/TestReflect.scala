package org.nerver.core

import org.nerver.core.annotation.{Mapping, Method}

/**
  * Created by LJT on 2016/8/14.
  */
class TestReflect {
  val str = "test"

  @Mapping(value = "", method = Method.GET)
  def print(str1: String): Unit = {
    println(str + "  " + str1)
  }
}
