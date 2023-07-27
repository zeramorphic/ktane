ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file("."))
  .settings(
    name := "ktane",
    idePackagePrefix := Some("com.zeramorphic.ktane")
  )

Compile / unmanagedJars += file("lib/opencv-480.jar")
