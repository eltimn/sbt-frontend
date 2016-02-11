package sbtfrontend

import sbt._
import Keys._
import complete.DefaultParsers._

import org.slf4j.impl.StaticLoggerBinder
import net.liftweb.common._
import com.github.eirslett.maven.plugins.frontend.lib._

import FrontendPlugin.autoImport.FrontendKeys.{frontendFactory, nodeProxies}

object FrontendInputTask {
  def apply(
    key: InputKey[Unit],
    func: (FrontendPluginFactory, String) => Box[Unit]
  ): Def.Initialize[InputTask[Unit]] = {
    Def.inputTask {
      val args = spaceDelimited("<arg>").parsed
      val log = streams.value.log
      StaticLoggerBinder.sbtLogger = log
      func((frontendFactory in key).value, args.mkString(" ")) match {
        case Failure(msg, Full(e), _) => throw e
        case _ =>
      }
    }
  }
}

object FrontendProxyInputTask {
  def apply(
    key: InputKey[Unit],
    func: (FrontendPluginFactory, String, Seq[ProxyConfig.Proxy]) => Box[Unit]
  ): Def.Initialize[InputTask[Unit]] = {
    Def.inputTask {
      val args = spaceDelimited("<arg>").parsed
      val log = streams.value.log
      StaticLoggerBinder.sbtLogger = log
      func(
        (frontendFactory in key).value,
        args.mkString(" "),
        (nodeProxies in key).value
      ) match {
        case Failure(msg, Full(e), _) => throw e
        case _ =>
      }
    }
  }
}
