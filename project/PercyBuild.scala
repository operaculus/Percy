import sbt._
import sbt.Keys._

import com.github.retronym.SbtOneJar

object PercyBuild extends Build {

  lazy val percy = Project(
    id = "percy",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "Percy",
      organization := "org.semantic",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.2",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies += "net.liftweb" %% "lift-json" % "2.5.1",
      libraryDependencies += "log4j" % "log4j" % "1.2.16",
      libraryDependencies += "com.typesafe" % "config" % "1.0.0",
      libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "8.1.8.v20121106",
      libraryDependencies += "org.eclipse.jetty" % "jetty-servlet" % "8.1.8.v20121106"
    ) ++ SbtOneJar.oneJarSettings
  )
}
