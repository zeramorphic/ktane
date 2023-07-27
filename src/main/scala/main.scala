package com.zeramorphic.ktane

import modules.*

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
  val dimensions = BombDimensions(2, 3)

  // interactions.pickUpBomb()

  //  Keypad(interactions).solve()

  val edgework = ReadEdgework.read(interactions)
  println(edgework)

  val moduleLocations = DetectModules.detect(interactions, dimensions)
  // interactions.screenshotAllModules(moduleLocations, dimensions)

  val moduleTypes = moduleLocations.iterator
    .map(location => CategoriseModule.categorise(interactions, location, dimensions))
    .toList

  for (location, ty) <- moduleLocations.iterator.zip(moduleTypes) do {
    ty match {
      case "keypad" =>
        interactions.selectModule(location, dimensions)
        ImageConversion.writeToFile(interactions.screenshotSelectedModule(), "keypad")
        Keypad(interactions).solve()
        interactions.deselect()
      case "memory" =>
        interactions.selectModule(location, dimensions)
        ImageConversion.writeToFile(interactions.screenshotSelectedModule(), "memory")
        Memory(interactions).solve()
        interactions.deselect()
      case "password" =>
        interactions.selectModule(location, dimensions)
        ImageConversion.writeToFile(interactions.screenshotSelectedModule(), "password")
        Password(interactions).solve()
        interactions.deselect()
      case _ => println("do not know how to solve " + ty)
    }

    Thread.sleep(100)
  }
}
