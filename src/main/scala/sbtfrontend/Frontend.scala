package sbtfrontend

import scala.collection.JavaConverters._

import net.liftweb.common._
import com.github.eirslett.maven.plugins.frontend.lib.{
  FrontendPluginFactory, ProxyConfig
}

object Frontend {
  import FrontendPlugin.autoImport.FrontendKeys._

  private def tryo[T](f: => T): Box[T] = {
    try {
      Full(f)
    } catch {
      case e: Throwable => Failure(e.getMessage, Full(e), Empty)
    }
  }

  def nodeInstall(
    factory: FrontendPluginFactory,
    nodeVersion: String,
    npmVersion: String,
    nodeDownloadRoot: String,
    npmDownloadRoot: String,
    proxies: Seq[ProxyConfig.Proxy]
  ): Box[Unit] = {
    tryo {
      factory
        .getNodeAndNPMInstaller(new ProxyConfig(proxies.asJava))
        .install(
          nodeVersion,
          npmVersion,
          nodeDownloadRoot,
          npmDownloadRoot
        )
    }
  }

  def npm(factory: FrontendPluginFactory, arguments: String, proxies: Seq[ProxyConfig.Proxy]): Box[Unit] =
    tryo { factory.getNpmRunner(new ProxyConfig(proxies.asJava)).execute(arguments) }

  def bower(factory: FrontendPluginFactory, arguments: String, proxies: Seq[ProxyConfig.Proxy]): Box[Unit] =
    tryo { factory.getBowerRunner(new ProxyConfig(proxies.asJava)).execute(arguments) }

  def grunt(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getGruntRunner().execute(arguments) }

  def gulp(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getGulpRunner().execute(arguments) }

  def jspm(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getJspmRunner().execute(arguments) }

  def karma(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getKarmaRunner().execute(arguments) }

  def webpack(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getWebpackRunner().execute(arguments) }

  def ember(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getEmberRunner().execute(arguments) }
}
