sbt-frontend
============

[![Build Status](https://travis-ci.org/eltimn/sbt-frontend.svg?branch=master)](https://travis-ci.org/eltimn/sbt-frontend)

[SBT](http://www.scala-sbt.org/) plugin for managing frontend code (node and npm, grunt, gulp, bower, etc.)

SBT version of [frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin).

From the readme:

> This plugin downloads/installs Node and NPM locally for your project, runs NPM install, and then any combination of [Bower](http://bower.io/), [Grunt](http://gruntjs.com/), [Gulp](http://gulpjs.com/), [Jspm](http://jspm.io), [Karma](http://karma-runner.github.io/), or [Webpack](http://webpack.github.io/).
> It's supposed to work on Windows, OS X and Linux.

Setup
-----

Add the following to your _project/plugins.sbt_ file:

```scala
addSbtPlugin("com.eltimn" % "sbt-frontend" % "0.1.0")
```

Enable the plugin in your _build.sbt_ file with:

```scala
enablePlugins(FrontendPlugin)
```

Usage
-----

Upon loading SBT, Node and npm with be automatically installed locally. Also, if `package.json` was modified since the last time SBT was loaded, `npm install` will be called. The same goes for `bower.json`, if it exists.

The plugin makes available the following `InputTask`s. Note that you must first install any apps you want to use with `npm install`.

| Task          | Description   |
| ------------- | ------------- |
| nodeInstall   | Installs Node and npm |
| npm           | Runs npm commands |
| bower         | Runs bower commands |
| grunt         | Runs grunt commands |
| gulp          | Runs gulp commands |
| jspm          | Runs jspm commands |
| karma         | Runs karma commands |
| webpack       | Runs webpack commands |
| ember         | Runs ember commands |

Since these are `InputTask`s you can call them with command line arguments. E.g.:

```bash
sbt> npm install gulp --save-dev
sbt> bower install
sbt> gulp build
```

To create a standard `Task` using one of the `InputTask`s, use the following:

```scala
val myTask = taskKey[Unit]("My task")
myTask := gulp.toTask(" build").value
```

The following `FrontendKeys` are also available:

| Setting              | Description   |
| -------------------- | ------------- |
| frontendFactory      | The FrontendFactory instance |
| nodeVersion          | The version of Node.js to install |
| npmVersion           | The version of NPM to install |
| nodeInstallDirectory | The base directory for installing node and npm |
| nodeWorkingDirectory | The base directory for running node and npm |
| nodeDownloadRoot     | Where to download Node.js binary from |
| npmDownloadRoot      | Where to download NPM binary from |
| nodeProxies          | Seq of proxies for downloader |

Credits
-------

Most of the heavy lifting is done by [frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin).

This project was also inspired by:

* [gradle-node-plugin](https://github.com/srs/gradle-node-plugin)
* [sbt-node-installer](https://github.com/backtick/sbt-node-installer)

References
----------
[testing-sbt-plugins](http://eed3si9n.com/testing-sbt-plugins)

License
-------

Published under [The MIT License](https://opensource.org/licenses/MIT)
