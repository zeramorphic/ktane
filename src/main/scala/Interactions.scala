package com.zeramorphic.ktane

import org.opencv.core.*

import java.awt.event.InputEvent
import java.awt.{GraphicsDevice, Rectangle, Robot}

class Interactions(screen: GraphicsDevice):
  private val robot: Robot = Robot()
  println(s"using screen $screen")

  /**
   * True if the bomb is currently reversed.
   */
  private var reversed: Boolean = false

  /**
   * The location of a module if one is currently selected.
   */
  private var moduleSelected: Option[ModuleLocation] = None

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
    Thread.sleep(100)
    screenshot(Rect(835, 375, 285, 285))
  }

  def screenshotOnModule(rect: Rect): Mat = {
    moveAway()
    Thread.sleep(100)
    screenshot(Rect(835 + rect.x, 375 + rect.y, rect.width, rect.height))
  }

  def screenshot(rect: Rect): Mat =
    ImageConversion.bufferedImageToMat(robot.createScreenCapture(Rectangle(
      screen.getDefaultConfiguration.getBounds.x + rect.x, screen.getDefaultConfiguration.getBounds.y + rect.y,
      rect.width, rect.height)))

  /**
   * Moves the mouse out of the way.
   */
  def moveAway(): Unit = moveOnScreen(Point(960, 950))

  def selectModule(location: ModuleLocation, dimensions: BombDimensions): Unit = {
    if moduleSelected.nonEmpty then {
      if moduleSelected.get == location then return else deselect()
    }
    if reversed != location.reverse then flipBomb()
    pressOnScreen(dimensions.position(location))
    Thread.sleep(500)

    moduleSelected = Some(location)
  }

  /**
   * Flips the bomb from obverse to reverse, or vice versa.
   * Assumes that the bomb is selected, but no module is selected.
   */
  def flipBomb(): Unit = {
    moveOnScreen(Point(880, 540))
    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
    Thread.sleep(100)
    moveOnScreen(Point(320, 540))
    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
    Thread.sleep(100)
    deselect()
    pickUpBomb()
    // Sleep for some extra time because of the animations we queued up by
    // deselecting then reselecting the bomb.
    Thread.sleep(500)

    reversed = !reversed
  }

  def pickUpBomb(): Unit = {
    pressOnScreen(Point(960, 600))
    Thread.sleep(500)
  }

  def pressOnScreen(point: Point): Unit = {
    moveOnScreen(point)
    pressOnScreen()
  }

  def pressOnScreen(): Unit = {
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
  }

  def moveOnModule(point: Point): Unit = moveOnScreen(Point(835 + point.x, 375 + point.y))

  def moveOnScreen(point: Point): Unit =
    robot.mouseMove(screen.getDefaultConfiguration.getBounds.x + point.x.toInt,
      screen.getDefaultConfiguration.getBounds.y + point.y.toInt)

  def deselect(): Unit = {
    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
    Thread.sleep(300)

    moduleSelected = None
  }

  /**
   * Presses the given point relative to the currently selected module.
   */
  def pressOnModule(point: Point): Unit = pressOnScreen(Point(835 + point.x, 375 + point.y))

  def mouseDown(): Unit = robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)

  def mouseUp(): Unit = robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
