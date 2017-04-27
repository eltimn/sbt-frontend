package sbtfrontend

import sbt._
import sbt.Keys._

import java.util.jar.JarFile
import scala.util.control.NonFatal

import org.slf4j.impl.StaticLoggerBinder
import net.liftweb.common.{ Failure, Full }
import com.github.eirslett.maven.plugins.frontend.lib.{
  FrontendPluginFactory, NodeInstaller, NPMInstaller, ProxyConfig
}

object Defaults {
  val nodeVersion = "v6.10.1"
  val npmVersion = "3.10.10"
  val nodeDownloadRoot = NodeInstaller.DEFAULT_NODEJS_DOWNLOAD_ROOT
  val npmDownloadRoot = NPMInstaller.DEFAULT_NPM_DOWNLOAD_ROOT
  val npmRegistryUrl: Option[String] = None
}

object FrontendPlugin extends AutoPlugin {

  object autoImport {
    val nodeInstall = taskKey[Unit]("Installs node.js and npm locally.")
    val npm = inputKey[Unit]("Runs npm commands")
    val bower = inputKey[Unit]("Runs bower commands")
    val grunt = inputKey[Unit]("Runs grunt commands")
    val gulp = inputKey[Unit]("Runs gulp commands")
    val jspm = inputKey[Unit]("Runs jspm commands")
    val karma = inputKey[Unit]("Runs karma commands")
    val webpack = inputKey[Unit]("Runs webpack commands")
    val ember = inputKey[Unit]("Runs ember commands")
    val webjars = taskKey[Unit]("Extract web jar assets")
    val frontendCleanDeps = taskKey[Unit]("Remove frontend dependencies. Must reload project afterwards.")
    val frontendCleanAll = taskKey[Unit]("Remove Node, NPM, and dependencies. Must reload project afterwards.")

    object FrontendKeys {
      val frontendFactory = settingKey[FrontendPluginFactory]("The FrontendFactory instance")
      val nodeVersion = settingKey[String](s"The version of Node.js to install. Default: ${Defaults.nodeVersion}")
      val npmVersion = settingKey[String](s"The version of NPM to install. Default: ${Defaults.npmVersion}")
      val nodeInstallDirectory = settingKey[File](s"The base directory for installing node and npm. Default: baseDirectory/.frontend")
      val nodeWorkingDirectory = settingKey[File](s"The base directory for running node and npm. Default: baseDirectory")
      val nodeDownloadRoot = settingKey[String](s"Where to download Node.js binary from. Default: ${Defaults.nodeDownloadRoot}")
      val npmDownloadRoot = settingKey[String](s"Where to download NPM binary from. Default: ${Defaults.npmDownloadRoot}")
      val npmRegistryUrl = settingKey[Option[String]](s"NPM registry URL. Default: ${Defaults.npmRegistryUrl}")
      val nodeProxies = settingKey[Seq[ProxyConfig.Proxy]]("Seq of proxies for downloader.")
      val npmFile = settingKey[File]("package.json")
      val bowerFile = settingKey[File]("bower.json")
    }

    lazy val frontendSettings: Seq[Def.Setting[_]] = {
      import FrontendKeys._

      Seq(
        nodeVersion := Defaults.nodeVersion,
        npmVersion := Defaults.npmVersion,
        nodeInstallDirectory := baseDirectory.value / ".frontend",
        nodeWorkingDirectory := baseDirectory.value,
        nodeDownloadRoot := Defaults.nodeDownloadRoot,
        npmDownloadRoot := Defaults.npmDownloadRoot,
        npmRegistryUrl := Defaults.npmRegistryUrl,
        npmFile := nodeWorkingDirectory.value / "package.json",
        bowerFile := nodeWorkingDirectory.value / "bower.json",
        nodeProxies := Nil,
        frontendFactory := {
          new FrontendPluginFactory(nodeWorkingDirectory.value, nodeInstallDirectory.value)
        },
        nodeInstall := {
          StaticLoggerBinder.sbtLogger = streams.value.log
          Frontend.nodeInstall(
            frontendFactory.value,
            nodeVersion.value,
            npmVersion.value,
            nodeDownloadRoot.value,
            npmDownloadRoot.value,
            nodeProxies.value
          ) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
        npm <<= FrontendProxyInputTask(npm, Frontend.npm _),
        bower <<= FrontendProxyInputTask(bower, Frontend.bower _),
        grunt <<= FrontendInputTask(grunt, Frontend.grunt _),
        gulp <<= FrontendInputTask(gulp, Frontend.gulp _),
        jspm <<= FrontendInputTask(jspm, Frontend.jspm _),
        karma <<= FrontendInputTask(karma, Frontend.karma _),
        webpack <<= FrontendInputTask(webpack, Frontend.webpack _),
        ember <<= FrontendInputTask(ember, Frontend.ember _),
        webjars := {
          for {
            file <- (dependencyClasspath in Compile).value.map(_.data)
            jar <- tryo(new JarFile(file))
          } {
            Frontend.extractWebjarAssets(jar, target.value / "webjars")
          }
        },
        frontendCleanDeps := {
          IO.delete(nodeInstallDirectory.value / "bower.json.md5")
          IO.delete(nodeInstallDirectory.value / "package.json.md5")
          IO.delete(nodeWorkingDirectory.value / "bower_components")
          IO.delete(nodeWorkingDirectory.value / "node_modules")
        },
        frontendCleanAll := {
          IO.delete(nodeWorkingDirectory.value / ".frontend")
          frontendCleanDeps.value
        },
        onLoad in Global := {
          val onLoadFunc = (s: State) => {
            StaticLoggerBinder.sbtLogger = s.log

            // install node and npm
            Frontend.nodeInstall(
              frontendFactory.value,
              nodeVersion.value,
              npmVersion.value,
              nodeDownloadRoot.value,
              npmDownloadRoot.value,
              nodeProxies.value
            ) match {
              case Failure(msg, Full(e), _) => throw e
              case _ =>
            }

            def runIfUpdated(file: File)(func: => Unit): Unit = {
              val name = file.getName
              val fileDigest = FrontendUtils.digest(file)
              val digestFile = nodeInstallDirectory.value / s"${name}.md5"
              val savedDigest: Option[String] = {
                if (digestFile.exists) {
                  Some(IO.read(digestFile))
                } else {
                  None
                }
              }

              if (savedDigest.map(_ != fileDigest).getOrElse(true)) {
                IO.write(digestFile, fileDigest)
                s.log.info(s"${name} has changed. Running install task.")
                func
              }
            }

            // npm install
            if (npmFile.value.exists) {
              runIfUpdated(npmFile.value) {
                Frontend.npm(
                  frontendFactory.value,
                  "install",
                  nodeProxies.value
                ) match {
                  case Failure(msg, Full(e), _) => throw e
                  case _ =>
                }
              }
            }

            // bower install
            if (bowerFile.value.exists) {
              runIfUpdated(bowerFile.value) {
                Frontend.bower(
                  frontendFactory.value,
                  "install",
                  nodeProxies.value
                ) match {
                  case Failure(msg, Full(e), _) => throw e
                  case _ =>
                }
              }
            }

            s
          }

          val previous = (onLoad in Global).value
          onLoadFunc compose previous
        }
      )
    }
  }

  // add the keys to the plugin
  import autoImport._

  override def projectSettings = frontendSettings

  private def tryo[T](f: => T): Option[T] = {
    try {
      Option(f)
    } catch {
      case NonFatal(e) =>
        None
    }
  }
}
