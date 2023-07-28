ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file("."))
  .settings(
    name := "ktane",
    idePackagePrefix := Some("com.zeramorphic.ktane")
  )

libraryDependencies ++= Seq(
  "net.sourceforge.tess4j" % "tess4j" % "5.7.0",
  "com.github.kwhat" % "jnativehook" % "2.2.2",
)

Compile / unmanagedJars += file("lib/opencv-480.jar")
