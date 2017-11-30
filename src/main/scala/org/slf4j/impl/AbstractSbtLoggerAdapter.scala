package org.slf4j.impl

import org.slf4j.helpers.MarkerIgnoringBase
import org.slf4j.{Logger => SLF4JLogger}
import sbt.util.{Logger => SbtLogger}

import scala.compat.Platform.EOL

abstract class AbstractSbtLoggerAdapter(protected val log: SbtLogger) extends MarkerIgnoringBase with SLF4JLogger {
  override def error(p1: String, p2: Throwable): Unit = log error toMessageWithCause(p1,p2)

  override def error(p1: String, p2: scala.Any, p3: scala.Any): Unit = log error toMessage(p1,p2,p3)

  override def error(p1: String, p2: scala.Any): Unit = log error toMessage(p1,p2)

  override def error(p1: String): Unit = log error p1

  override def warn(p1: String, p2: Throwable): Unit = log warn toMessageWithCause(p1,p2)

  override def warn(p1: String, p2: scala.Any, p3: scala.Any): Unit = log warn toMessage(p1,p2,p3)

  override def warn(p1: String, p2: scala.Any): Unit = log warn toMessage(p1,p2)

  override def warn(p1: String): Unit = log warn p1

  override def info(p1: String, p2: Throwable): Unit = log info toMessageWithCause(p1,p2)

  override def info(p1: String, p2: scala.Any, p3: scala.Any): Unit = log info toMessage(p1,p2,p3)

  override def info(p1: String, p2: scala.Any): Unit = log info toMessage(p1,p2)

  override def info(p1: String): Unit = log info p1

  override def debug(p1: String, p2: Throwable): Unit = log debug toMessageWithCause(p1,p2)

  override def debug(p1: String, p2: scala.Any, p3: scala.Any): Unit = log debug toMessage(p1,p2,p3)

  override def debug(p1: String, p2: scala.Any): Unit = log debug toMessage(p1,p2)

  override def debug(p1: String): Unit = log debug p1

  override def trace(p1: String, p2: Throwable): Unit = log trace p2

  override def trace(p1: String, p2: scala.Any, p3: scala.Any): Unit = log debug toMessage(p1,p2,p3)

  override def trace(p1: String, p2: scala.Any): Unit = log debug toMessage(p1,p2)

  override def trace(p1: String): Unit = log debug p1

  override def isTraceEnabled = true

  override def isDebugEnabled = true

  override def isInfoEnabled = true

  override def isWarnEnabled = true

  override def isErrorEnabled = true

  protected def toMessageWithCause(message: String, cause: Throwable): String =
    message + ": " + cause.getStackTrace.mkString("", EOL, EOL)

  protected def toMessage(message: String, args: Any*): String =
    message +": "+args mkString ","

  protected def toSeq(array: Array[Any]): Seq[Any] = array.toSeq
}
