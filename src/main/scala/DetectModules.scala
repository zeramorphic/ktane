package com.zeramorphic.ktane

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

import java.util

object DetectModules:
  def detect(interactions: Interactions, dimensions: BombDimensions): List[ModuleLocation] = {
    val obverse = dimensions.obverseModuleLocations()
      .filter(location => hasModuleAt(interactions, dimensions.position(location)))
    interactions.flipBomb()
    val reverse = dimensions.reverseModuleLocations()
      .filter(location => hasModuleAt(interactions, dimensions.position(location)))
    interactions.flipBomb()
    (obverse ++ reverse).toList
  }

  private def hasModuleAt(interactions: Interactions, point: Point): Boolean = {
    // Pressing will also focus the window.
    interactions.moveAway()
    interactions.pressOnScreen()
    Thread.sleep(100)
    val before = interactions.screenshot(Rect(100, 100, 1920 - 100, 1080 - 100))
    interactions.moveOnScreen(point)
    Thread.sleep(100)
    val after = interactions.screenshot(Rect(100, 100, 1920 - 100, 1080 - 100))

    val subtract = Mat()
    Core.subtract(after, before, subtract)
    val subtractSingleChannel = Mat()
    // Channel 2 is the red channel.
    // The yellow outline has a lot of red, and the blue bomb casing has little.
    Core.extractChannel(subtract, subtractSingleChannel, 2)
    val threshold = Mat()
    Imgproc.threshold(subtractSingleChannel, threshold, 50, 255, Imgproc.THRESH_BINARY)
    val contours = util.ArrayList[MatOfPoint]()
    Imgproc.findContours(threshold, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
    val largeContours = contours.toArray(Array[Mat]()).filter(mat => 50_000 <= Imgproc.contourArea(mat))

    largeContours.length match {
      case 0 => false
      case 1 => true
      case _ => throw Exception("expected at most one large contour: " + largeContours.mkString("Array(", ", ", ")"))
    }
  }
