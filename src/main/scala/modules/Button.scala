package com.zeramorphic.ktane
package modules

import org.opencv.core.{Core, Mat, Point, Rect}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

class Button(interactions: Interactions, edgework: Edgework) extends OneshotModule:
  override def solve(): Unit = {
    enum ButtonColour:
      case Red
      case Blue
      case Yellow
      case White

    enum ButtonText:
      case Abort
      case Detonate
      case Hold
      case Press

    val buttonText = interactions.screenshotOnModule(Rect(40, 148, 152, 40))
    val backgroundData = Array[Byte](0, 0, 0)
    buttonText.get(0, 0, backgroundData)
    val backgroundColours = backgroundData.map(value => Math.floorMod(value.toInt, 255))
    val colour = Seq(
      (Array(72, 56, 255), ButtonColour.Red),
      (Array(230, 95, 59), ButtonColour.Blue),
      (Array(47, 203, 254), ButtonColour.Yellow),
      (Array(255, 255, 255), ButtonColour.White),
    ).minBy((col, _) => col.zip(backgroundColours).map((a, b) => (a - b).abs).sum)._2

    // Extract the green channel.
    Core.extractChannel(buttonText, buttonText, 1)
    if colour == ButtonColour.Red || colour == ButtonColour.Blue then {
      Core.bitwise_not(buttonText, buttonText)
    }
    // Now the text is in a dark colour on a light background.
    Imgproc.threshold(buttonText, buttonText, 128, 255, Imgproc.THRESH_BINARY)

    val text = MatchTemplate.bestMatch(buttonText, Seq(
      (Button.Abort, ButtonText.Abort),
      (Button.Detonate, ButtonText.Detonate),
      (Button.Hold, ButtonText.Hold),
      (Button.Press, ButtonText.Press),
    ).iterator)._2

    val hold =
      if colour == ButtonColour.Blue && text == ButtonText.Abort then true
      else if edgework.batteries > 1 && text == ButtonText.Detonate then false
      else if colour == ButtonColour.White && edgework.lit.contains(Indicator.Car) then true
      else if edgework.batteries > 2 && edgework.lit.contains(Indicator.Frk) then false
      else if colour == ButtonColour.Yellow then true
      else if colour == ButtonColour.Red && text == ButtonText.Hold then false
      else true

    if hold then {
      interactions.moveOnModule(Point(115, 168))
      interactions.mouseDown()
      Thread.sleep(800)
      val strip = interactions.screenshotOnModule(Rect(245, 170, 1, 1))
      val data = Array[Byte](0, 0, 0)
      strip.get(0, 0, data)
      val stripColour = data.map(value => Math.floorMod(value.toInt, 255))
      val releaseOn = Seq(
        (Array(236, 105, 32), '4'),
        (Array(247, 247, 247), '1'),
        (Array(21, 206, 244), '5'),
        (Array(45, 46, 223), '1'),
      ).minBy((col, _) => col.zip(stripColour).map((a, b) => (a - b).abs).sum)._2
      BombTime.waitUntil(interactions, time => time.toString.contains(releaseOn))
      interactions.mouseUp()
    } else {
      interactions.pressOnModule(Point(115, 168))
    }
  }

object Button:
  private val Abort: Mat = Imgcodecs.imread("images/button/abort.png")
  private val Detonate: Mat = Imgcodecs.imread("images/button/detonate.png")
  private val Hold: Mat = Imgcodecs.imread("images/button/hold.png")
  private val Press: Mat = Imgcodecs.imread("images/button/press.png")

  Core.extractChannel(Abort, Abort, 0)
  Core.extractChannel(Detonate, Detonate, 0)
  Core.extractChannel(Hold, Hold, 0)
  Core.extractChannel(Press, Press, 0)
