package com.zeramorphic.ktane

import org.opencv.core.*

import java.awt.event.InputEvent
import java.awt.{GraphicsDevice, Rectangle, Robot}

class Interactions(screen: GraphicsDevice):
  private val robot: Robot = Robot()
  println(s"using screen $screen")

  private var reversed: Boolean = false

  /**
   * Saves screenshots of all modules with listed locations.
   */
  def screenshotAllModules(moduleLocations: Iterable[ModuleLocation], dimensions: BombDimensions): Unit = {
    for (module, i) <- moduleLocations.zipWithIndex do {
      selectModule(module, dimensions)
      val screenshot = screenshotSelectedModule()
      ImageConversion.writeToFile(screenshot, s"module$i")
      deselect()
    }
  }

  def screenshotSelectedModule(): Mat = {
    moveAway()
    screenshot(Rect(835, 375, 285, 285))
  }

  def screenshot(rect: Rect): Mat =
    ImageConversion.bufferedImageToMat(robot.createScreenCapture(Rectangle(
      screen.getDefaultConfiguration.getBounds.x + rect.x, screen.getDefaultConfiguration.getBounds.y + rect.y,
      rect.width, rect.height)))

  /**
   * Moves the mouse out of the way.
   */
  def moveAway(): Unit = move(Point(960, 950))

  def selectModule(location: ModuleLocation, dimensions: BombDimensions): Unit = {
    if reversed != location.reverse then flipBomb()
    press(dimensions.position(location))
    Thread.sleep(500)
  }

  /**
   * Flips the bomb from obverse to reverse, or vice versa.
   * Assumes that the bomb is selected, but no module is selected.
   */
  def flipBomb(): Unit = {
    move(Point(880, 540))
    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
    Thread.sleep(100)
    move(Point(320, 540))
    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
    Thread.sleep(100)
    deselect()
    pickUpBomb()
    // Sleep for some extra time because of the animations we queued up by
    // deselecting then reselecting the bomb.
    Thread.sleep(500)

    reversed = !reversed
  }

  def move(point: Point): Unit =
    robot.mouseMove(screen.getDefaultConfiguration.getBounds.x + point.x.toInt,
      screen.getDefaultConfiguration.getBounds.y + point.y.toInt)

  def pickUpBomb(): Unit = {
    press(Point(960, 600))
    Thread.sleep(500)
  }

  def press(point: Point): Unit = {
    move(point)
    press()
  }

  def press(): Unit = {
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
  }

  def deselect(): Unit = {
    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
    Thread.sleep(300)
  }
