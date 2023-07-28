package com.zeramorphic.ktane

import org.opencv.core.Core

import java.awt.GraphicsEnvironment
import java.time.Instant

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

  // Maze(interactions).solve()

  val edgework = ReadEdgework.read(interactions)
  println(edgework)

  //  modules.Wires(interactions, edgework).solve()

  val moduleLocations = DetectModules.detect(interactions, dimensions)
  // interactions.screenshotAllModules(moduleLocations, dimensions)

  val sockets = moduleLocations.iterator
    .map(location => {
      val name = CategoriseModule.categorise(interactions, location, dimensions)
      Socket(location, name, interactions, edgework)
    })
    .filter(socket =>
      if socket.module == null then {
        println("do not know how to solve " + socket.name);
        false
      }
      else true
    )
    .toList
    .sortBy(socket => -socket.module.priority)

  while !sockets.forall(socket => socket.module.solved) do {
    // Try to solve one of the modules.
    val now = Instant.now()
    val socket = sockets
      .filter(socket => !socket.checkNext.isAfter(now))
      .find(socket => !socket.module.solved)
    socket match {
      case Some(socket) =>
        // Try to solve this module.
        interactions.selectModule(socket.location, dimensions)
        socket.module.attemptSolve() match {
          case Some(instant) => socket.checkNext = instant
          case None =>
        }
      case None =>
        // All modules are on cooldown.
        // Select the module that will next be not on cooldown.
        val next = sockets
          .filter(socket => !socket.module.solved)
          .minBy(socket => socket.checkNext)
        interactions.selectModule(next.location, dimensions)
    }
    // Sleep for a small amount afterwards to make sure all clicks actually register.
    // In the `None` case, this stops us going into a spin loop.
    Thread.sleep(50)
  }
}
