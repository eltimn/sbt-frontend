package sbtfrontend

import sbt._
import scala.collection.JavaConverters._
import java.io.File
import java.util.jar.JarFile

import net.liftweb.common._
import com.github.eirslett.maven.plugins.frontend.lib.{
  FrontendPluginFactory, ProxyConfig
}

object Frontend {
  import FrontendPlugin.autoImport.FrontendKeys._

  def environmentVariables: Map[String, String] = Map()

  private def tryo[T](f: => T): Box[T] = {
    try {
      Full(f)
    } catch {
      case e: Throwable => Failure(e.getMessage, Full(e), Empty)
    }
  }

  def nodeInstall(
    factory: FrontendPluginFactory,
    nodePackageManager: NodePackageManager.Value,
    nodeVersion: String,
    npmVersion: String,
    yarnVersion: String,
    nodeDownloadRoot: String,
    npmDownloadRoot: String,
    yarnDownloadRoot: String,
    proxies: Seq[ProxyConfig.Proxy]
  ): Box[Unit] = {
    tryo {
      val proxyConfig: ProxyConfig =
        new ProxyConfig(proxies.asJava)

      factory.getNodeInstaller(proxyConfig)
        .setNodeVersion(nodeVersion)
        .setNodeDownloadRoot(nodeDownloadRoot)
        .setNpmVersion(npmVersion)
        .install()

      nodePackageManager match {
        case NodePackageManager.NPM =>
          factory.getNPMInstaller(proxyConfig)
            .setNodeVersion(nodeVersion)
            .setNpmVersion(npmVersion)
            .setNpmDownloadRoot(npmDownloadRoot)
            .install()

        case NodePackageManager.Yarn =>
          factory.getYarnInstaller(proxyConfig)
            .setYarnVersion(yarnVersion)
            .setYarnDownloadRoot(yarnDownloadRoot)
            .install()
      }
    }
  }

  def npm(factory: FrontendPluginFactory, arguments: String, proxies: Seq[ProxyConfig.Proxy]): Box[Unit] =
    tryo { factory.getNpmRunner(new ProxyConfig(proxies.asJava), null).execute(arguments, environmentVariables.asJava) }

  def yarn(factory: FrontendPluginFactory, arguments: String, proxies: Seq[ProxyConfig.Proxy]): Box[Unit] =
    tryo { factory.getYarnRunner(new ProxyConfig(proxies.asJava), null).execute(arguments, environmentVariables.asJava) }

  def bower(factory: FrontendPluginFactory, arguments: String, proxies: Seq[ProxyConfig.Proxy]): Box[Unit] =
    tryo { factory.getBowerRunner(new ProxyConfig(proxies.asJava)).execute(arguments, environmentVariables.asJava) }

  def grunt(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getGruntRunner().execute(arguments, environmentVariables.asJava) }

  def gulp(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getGulpRunner().execute(arguments, environmentVariables.asJava) }

  def jspm(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getJspmRunner().execute(arguments, environmentVariables.asJava) }

  def karma(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getKarmaRunner().execute(arguments, environmentVariables.asJava) }

  def webpack(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getWebpackRunner().execute(arguments, environmentVariables.asJava) }

  def ember(factory: FrontendPluginFactory, arguments: String): Box[Unit] =
    tryo { factory.getEmberRunner().execute(arguments, environmentVariables.asJava) }

  def extractWebjarAssets(jar: JarFile, dest: File): Unit = {
    val webjarDir = "META-INF/resources/webjars"

    jar.entries.asScala
      .filter(e => e.getName.startsWith(webjarDir) && !e.isDirectory)
      .foreach { entry =>
        // take out the version number from the path
        val destParts = entry.getName
          .stripPrefix(webjarDir)
          .split('/')
          .filter(_.nonEmpty)

        val destPath = (destParts.take(1) ++ destParts.drop(2)).mkString("/")
        val destFile = dest / destPath
        IO.transfer(jar.getInputStream(entry), destFile)
      }
  }
}
