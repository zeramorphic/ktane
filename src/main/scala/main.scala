package com.zeramorphic.ktane

import org.opencv.core.Core

import java.awt.GraphicsEnvironment

@main
def main(): Unit = {
  val opencvVersion = Core.VERSION
  println(s"loading OpenCV: $opencvVersion")
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
  System.loadLibrary("opencv_java480")
  println("loading done")

  val interactions = Interactions(GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices()(0))
  val readEdgework = ReadEdgework(interactions)
  readEdgework.read()
}
