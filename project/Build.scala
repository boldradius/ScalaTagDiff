import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "ScalaTagDiff"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "org.slf4j" % "slf4j-simple" % "1.6.4",
    "org.scalaz" % "scalaz-core_2.10" % "7.0.0",
    "org.scalaz" % "scalaz-effect_2.10" % "7.0.0",
    "org.scalaz" % "scalaz-concurrent_2.10" % "7.0.0",
    "org.webjars" % "requirejs" % "2.1.5",
    "com.chuusai" % "shapeless" % "2.0.0-M1" cross CrossVersion.full exclude("org.scala-stm", "scala-stm_2.10.0")
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalaVersion := "2.10.2",
    routesImport += "tools.PathBindables._",
    requireJs += "main.js"

  )

}
