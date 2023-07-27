package com.zeramorphic.ktane

import org.opencv.core.{Mat, Rect}

import java.awt.{GraphicsDevice, Rectangle, Robot}

class Interactions(screen: GraphicsDevice):
  private val robot: Robot = Robot()
  println(s"using screen $screen")

  def screenshot(rect: Rect): Mat =
    ImageConversion.bufferedImageToMat(robot.createScreenCapture(Rectangle(
      screen.getDefaultConfiguration.getBounds.x + rect.x, screen.getDefaultConfiguration.getBounds.y + rect.y,
      rect.width, rect.height)))
