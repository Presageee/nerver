package org.nerver.core.exception

/**
  * Created by LJT on 2016/8/21.
  */
class BaseException(msg: String) extends Exception{
  def getMsg():String = {
    msg
  }
}
