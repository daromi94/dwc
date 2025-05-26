ThisBuild / scalaVersion := "3.7.0"
ThisBuild / organization := "com.daromi.dwc"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file(".")).settings(name := "core")

libraryDependencies += "org.apache.pekko" %% "pekko-actor-typed" % "1.1.3"
libraryDependencies += "ch.qos.logback"    % "logback-classic"   % "1.5.18"
