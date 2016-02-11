package sbtfrontend

import sbt._
import java.security.MessageDigest

object FrontendUtils {
  def digest(file: File): String = {
    val md = MessageDigest.getInstance("MD5")
    md.digest(IO.readBytes(file)).map("%02x".format(_)).mkString
  }

  // http://stackoverflow.com/questions/23873031/sbt-plugin-how-to-list-files-output-by-incremental-recompilation/23876075#23876075
  def cachedFunction(cacheDirectory: File, inStyle: FilesInfo.Style)(action: Set[File] => Unit): Set[File] => Unit = {
    import Path._
    lazy val inCache = Difference.inputs(cacheDirectory / "in-cache", inStyle)
    inputs => {
      inCache(inputs) { inReport =>
        if (!inReport.modified.isEmpty) {
          action(inReport.modified)
        }
      }
    }
  }
}
