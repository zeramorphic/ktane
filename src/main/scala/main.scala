package com.zeramorphic.ktane

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.{NativeKeyEvent, NativeKeyListener}
import org.opencv.core.Core

import java.awt.GraphicsEnvironment
import java.time.{Duration, Instant}

@main
def main(): Unit = {
  val opencvVersion = Core.VERSION
  println(s"loading OpenCV: $opencvVersion")
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
  System.loadLibrary("opencv_java480")
  println("loading done")

  // One can press `Shift+Q` to immediately exit the program with code 2.
  GlobalScreen.registerNativeHook()
  GlobalScreen.addNativeKeyListener(new NativeKeyListener {
    override def nativeKeyTyped(nativeEvent: NativeKeyEvent): Unit = {
      // Forced quit by user.
      if nativeEvent.getKeyChar == 'Q' then System.exit(2)
    }
  })

  val interactions = Interactions(GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices()(0))
  val dimensions = BombDimensions(2, 3)

  // interactions.pickUpBomb()

  val edgework = ReadEdgework.read(interactions)
  println(edgework)

//  modules.ComplicatedWires(interactions, edgework).solve()
//  return

//  val module = modules.WhosOnFirst(interactions)
//  while !module.solved do module.attemptSolve() match {
//    case Some(instant) => Thread.sleep(Duration.between(Instant.now, instant).toMillis)
//    case None =>
//  }

  val moduleLocations = DetectModules.detect(interactions, dimensions)
  // interactions.screenshotAllModules(moduleLocations, dimensions)

  val sockets = moduleLocations.iterator
    .map(location => {
      val name = CategoriseModule.categorise(interactions, location, dimensions)
      Socket(location, name, interactions, edgework)
    })
    .filter(socket =>
      if socket.module == null then {
        println("do not know how to solve " + socket.name)
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

  // The native key listener is still running, so we must kill it here.
  System.exit(0)
}
