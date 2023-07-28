package com.zeramorphic.ktane
package modules

import org.opencv.core.{Core, Mat, Point, Rect}
import org.opencv.imgproc.Imgproc

class ComplicatedWires(interactions: Interactions, edgework: Edgework) extends OneshotModule:
  override def solve(): Unit = {
    val screenshot = interactions.screenshotSelectedModule()

    // Determine wire colours.
    val wires = screenshot.submat(Rect(29, 165, 210, 20))
    val wiresHsv = Mat()
    Imgproc.cvtColor(wires, wiresHsv, Imgproc.COLOR_BGR2HSV)
    val wiresSat = Mat()
    Core.extractChannel(wiresHsv, wiresSat, 1)
    Imgproc.threshold(wiresSat, wiresSat, 100, 255, Imgproc.THRESH_BINARY)
    // wiresSat is now white where there's wire colouring and black where there is not.

    val redChannel = Mat()
    Core.extractChannel(wires, redChannel, 2)
    Core.bitwise_and(redChannel, wiresSat, redChannel)
    Imgproc.medianBlur(redChannel, redChannel, 3)

    val blueChannel = Mat()
    Core.extractChannel(wires, blueChannel, 0)
    Core.bitwise_and(blueChannel, wiresSat, blueChannel)
    Imgproc.medianBlur(blueChannel, blueChannel, 3)

    val red = (0 to 5)
      .map(i => Core.minMaxLoc(redChannel.submat(Rect(35 * i, 0, 32, 20))).maxVal > 150)
      .toList

    val blue = (0 to 5)
      .map(i => Core.minMaxLoc(blueChannel.submat(Rect(35 * i, 0, 32, 20))).maxVal > 128)
      .toList

    // Determine the presence of LEDs.
    val led = (0 to 5)
      .map(i => {
        val data = Array[Byte](0, 0, 0)
        screenshot.get(40, 40 + 30 * i, data)
        data(0) < 0
      })
      .toList

    // Determine the presence of stars.
    val starsSubmat = screenshot.submat(Rect(30, 235, 210, 10))
    Core.extractChannel(starsSubmat, starsSubmat, 2)
    Imgproc.threshold(starsSubmat, starsSubmat, 100, 255, Imgproc.THRESH_BINARY)
    val star = (0 to 5)
      .map(i => Core.minMaxLoc(starsSubmat.submat(Rect(40 * i, 0, 10, 10))).minVal < 100)
      .toList

    println(red)
    println(blue)
    println(led)
    println(star)

    for i <- 0 to 5 do {
      val cut = (red(i), blue(i), led(i), star(i)) match {
        case (false, false, false, false) |
             (false, false, false, true) |
             (true, false, false, true) => true
        case (false, false, true, false) |
             (false, true, false, true) |
             (true, true, true, true) => false
        case (false, false, true, true) |
             (true, false, true, false) |
             (true, false, true, true) => edgework.batteries >= 2
        case (false, true, true, false) |
             (false, true, true, true) |
             (true, true, false, true) => edgework.plates.exists(plate => plate.contains(Port.Parallel))
        case (true, false, false, false) |
             (false, true, false, false) |
             (true, true, false, false) |
             (true, true, true, false) => edgework.lastDigit % 2 == 0
      }

      if cut then {
        interactions.pressOnModule(Point(40 + 37 * i, 200))
        Thread.sleep(200)
      }
    }
  }
