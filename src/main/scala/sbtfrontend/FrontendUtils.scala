package sbtfrontend

import sbt._
import java.security.MessageDigest

object FrontendUtils {
  def digest(file: File): String = {
    val md = MessageDigest.getInstance("MD5")
    md.digest(IO.readBytes(file)).map("%02x".format(_)).mkString
  }
}
