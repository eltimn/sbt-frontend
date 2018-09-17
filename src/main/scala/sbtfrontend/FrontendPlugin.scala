package sbtfrontend

import sbt._
import sbt.Keys._
import complete.DefaultParsers._

import java.util.jar.JarFile
import scala.util.control.NonFatal

import org.slf4j.impl.SbtStaticLoggerBinder
import net.liftweb.common.{ Failure, Full }
import com.github.eirslett.maven.plugins.frontend.lib._

object NodePackageManager extends Enumeration {
  type NodePackageManager = Value
  val NPM, Yarn = Value
}

object Defaults {
  val nodeVersion = "v8.9.1"
  val npmVersion = "5.5.1"
  val yarnVersion = "v0.27.5"
  val nodeDownloadRoot = NodeInstaller.DEFAULT_NODEJS_DOWNLOAD_ROOT
  val npmDownloadRoot = NPMInstaller.DEFAULT_NPM_DOWNLOAD_ROOT
  val yarnDownloadRoot = YarnInstaller.DEFAULT_YARN_DOWNLOAD_ROOT
  val npmRegistryUrl: Option[String] = None
  val nodePackageManager = NodePackageManager.NPM
  val nodePackageManagerInstallCmd = "install"
}

object FrontendPlugin extends AutoPlugin {

  object autoImport {
    val nodeInstall = taskKey[Unit]("Installs node.js and npm/yarn locally.")
    val nodePackageManager = settingKey[NodePackageManager.Value]("npm/yarn")
    val npm = inputKey[Unit]("Runs npm commands")
    val yarn = inputKey[Unit]("Runs yarn commands")
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
      val yarnVersion = settingKey[String](s"The version of Yarn to install. Default: ${Defaults.yarnVersion}")
      val nodeInstallDirectory = settingKey[File](s"The base directory for installing node and npm. Default: baseDirectory/.frontend")
      val nodeWorkingDirectory = settingKey[File](s"The base directory for running node and npm. Default: baseDirectory")
      val nodeDownloadRoot = settingKey[String](s"Where to download Node.js binary from. Default: ${Defaults.nodeDownloadRoot}")
      val npmDownloadRoot = settingKey[String](s"Where to download NPM binary from. Default: ${Defaults.npmDownloadRoot}")
      val yarnDownloadRoot = settingKey[String](s"Where to download Yarn binary from. Default: ${Defaults.yarnDownloadRoot}")
      val npmRegistryUrl = settingKey[Option[String]](s"NPM registry URL. Default: ${Defaults.npmRegistryUrl}")
      val nodeProxies = settingKey[Seq[ProxyConfig.Proxy]]("Seq of proxies for downloader.")
      val nodePackageManagerInstallCmd = settingKey[String](s"Node Package manager project installation command, triggered for every sbt boot if npmFile has changed. Default: ${Defaults.nodePackageManagerInstallCmd}")
      val npmFile = settingKey[File]("package.json")
      val bowerFile = settingKey[File]("bower.json")
    }

    lazy val frontendSettings: Seq[Def.Setting[_]] = {
      import FrontendKeys._

      Seq(
        nodePackageManager := Defaults.nodePackageManager,
        nodePackageManagerInstallCmd := Defaults.nodePackageManagerInstallCmd,
        nodeVersion := Defaults.nodeVersion,
        npmVersion := Defaults.npmVersion,
        yarnVersion := Defaults.yarnVersion,
        nodeInstallDirectory := baseDirectory.value / ".frontend",
        nodeWorkingDirectory := baseDirectory.value,
        nodeDownloadRoot := Defaults.nodeDownloadRoot,
        npmDownloadRoot := Defaults.npmDownloadRoot,
        yarnDownloadRoot := Defaults.yarnDownloadRoot,
        npmRegistryUrl := Defaults.npmRegistryUrl,
        npmFile := nodeWorkingDirectory.value / "package.json",
        bowerFile := nodeWorkingDirectory.value / "bower.json",
        nodeProxies := Nil,
        frontendFactory := {
          new FrontendPluginFactory(nodeWorkingDirectory.value, nodeInstallDirectory.value)
        },
        nodeInstall := {
          SbtStaticLoggerBinder.sbtLogger = streams.value.log
          Frontend.nodeInstall(
            frontendFactory.value,
            nodePackageManager.value,
            nodeVersion.value,
            npmVersion.value,
            yarnVersion.value,
            nodeDownloadRoot.value,
            npmDownloadRoot.value,
            yarnDownloadRoot.value,
            nodeProxies.value
          ) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
        npm := {
          val args = spaceDelimited("<arg>").parsed
          val log = streams.value.log
          SbtStaticLoggerBinder.sbtLogger = log
          Frontend.npm(
            frontendFactory.value,
            args.mkString(" "),
            nodeProxies.value
          ) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
        yarn := {
          val args = spaceDelimited("<arg>").parsed
          val log = streams.value.log
          SbtStaticLoggerBinder.sbtLogger = log
          Frontend.yarn(
            frontendFactory.value,
            args.mkString(" "),
            nodeProxies.value
          ) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
        bower := {
          val args = spaceDelimited("<arg>").parsed
          val log = streams.value.log
          SbtStaticLoggerBinder.sbtLogger = log
          Frontend.bower(
            frontendFactory.value,
            args.mkString(" "),
            nodeProxies.value
          ) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
        grunt := {
          val args = spaceDelimited("<arg>").parsed
          val log = streams.value.log
          SbtStaticLoggerBinder.sbtLogger = log
          Frontend.grunt(frontendFactory.value, args.mkString(" ")) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
        gulp := {
          val args = spaceDelimited("<arg>").parsed
          val log = streams.value.log
          SbtStaticLoggerBinder.sbtLogger = log
          Frontend.gulp(frontendFactory.value, args.mkString(" ")) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
        jspm := {
          val args = spaceDelimited("<arg>").parsed
          val log = streams.value.log
          SbtStaticLoggerBinder.sbtLogger = log
          Frontend.jspm(frontendFactory.value, args.mkString(" ")) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
        karma := {
          val args = spaceDelimited("<arg>").parsed
          val log = streams.value.log
          SbtStaticLoggerBinder.sbtLogger = log
          Frontend.karma(frontendFactory.value, args.mkString(" ")) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
        webpack := {
          val args = spaceDelimited("<arg>").parsed
          val log = streams.value.log
          SbtStaticLoggerBinder.sbtLogger = log
          Frontend.webpack(frontendFactory.value, args.mkString(" ")) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
        ember := {
          val args = spaceDelimited("<arg>").parsed
          val log = streams.value.log
          SbtStaticLoggerBinder.sbtLogger = log
          Frontend.ember(frontendFactory.value, args.mkString(" ")) match {
            case Failure(msg, Full(e), _) => throw e
            case _ =>
          }
        },
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
            SbtStaticLoggerBinder.sbtLogger = s.log

            // install node and npm/yarn
            Frontend.nodeInstall(
              frontendFactory.value,
              nodePackageManager.value,
              nodeVersion.value,
              npmVersion.value,
              yarnVersion.value,
              nodeDownloadRoot.value,
              npmDownloadRoot.value,
              yarnDownloadRoot.value,
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

            // npm/yarn install
            if (npmFile.value.exists) {
              runIfUpdated(npmFile.value) {
                nodePackageManager.value match {
                  case NodePackageManager.NPM =>
                    Frontend.npm(
                      frontendFactory.value,
                      nodePackageManagerInstallCmd.value,
                      nodeProxies.value
                    ) match {
                      case Failure(msg, Full(e), _) => throw e
                      case _ =>
                    }
                  case NodePackageManager.Yarn =>
                    Frontend.yarn(
                      frontendFactory.value,
                      nodePackageManagerInstallCmd.value,
                      nodeProxies.value
                    ) match {
                      case Failure(msg, Full(e), _) => throw e
                      case _ =>
                    }
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
