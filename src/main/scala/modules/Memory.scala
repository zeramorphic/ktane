package com.zeramorphic.ktane
package modules

import org.opencv.core.{Mat, Point, Rect}
import org.opencv.imgcodecs.Imgcodecs

import scala.collection.mutable.ArrayBuffer

class Memory(interactions: Interactions):
  def solve(): Unit = {
    val labels = ArrayBuffer[Int]()
    val positions = ArrayBuffer[Int]()

    // Buttons are zero-indexed.
    // Labels are as written.

    {
      // Stage 1
      val (display, buttons) = state()
      display match {
        case 1 | 2 => labels.append(buttons(1)); positions.append(1); pressButton(1)
        case 3 => labels.append(buttons(2)); positions.append(2); pressButton(2)
        case 4 => labels.append(buttons(3)); positions.append(3); pressButton(3)
      }
    }

    Thread.sleep(3300)

    {
      // Stage 2
      val (display, buttons) = state()
      display match {
        case 1 => labels.append(4); positions.append(buttons.indexOf(4)); pressButton(buttons.indexOf(4))
        case 2 | 4 => labels.append(buttons(positions(0))); positions.append(positions(0)); pressButton(positions(0))
        case 3 => labels.append(buttons.head); positions.append(0); pressButton(0)
      }
    }

    Thread.sleep(3300)

    {
      // Stage 3
      val (display, buttons) = state()
      display match {
        case 1 => labels.append(labels(1)); positions.append(buttons.indexOf(labels(1))); pressButton(buttons.indexOf(labels(1)))
        case 2 => labels.append(labels(0)); positions.append(buttons.indexOf(labels(0))); pressButton(buttons.indexOf(labels(0)))
        case 3 => labels.append(buttons(2)); positions.append(2); pressButton(2)
        case 4 => labels.append(4); positions.append(buttons.indexOf(4)); pressButton(buttons.indexOf(4))
      }
    }

    Thread.sleep(3300)

    {
      // Stage 4
      val (display, buttons) = state()
      display match {
        case 1 => labels.append(buttons(positions(0))); positions.append(positions(0)); pressButton(positions(0))
        case 2 => labels.append(buttons.head); positions.append(0); pressButton(0)
        case 3 | 4 => labels.append(buttons(positions(1))); positions.append(positions(1)); pressButton(positions(1))
      }
    }

    Thread.sleep(3300)

    {
      // Stage 5
      val (display, buttons) = state()
      display match {
        case 1 => labels.append(labels(0)); positions.append(buttons.indexOf(labels(0))); pressButton(buttons.indexOf(labels(0)))
        case 2 => labels.append(labels(1)); positions.append(buttons.indexOf(labels(1))); pressButton(buttons.indexOf(labels(1)))
        case 3 => labels.append(labels(3)); positions.append(buttons.indexOf(labels(3))); pressButton(buttons.indexOf(labels(3)))
        case 4 => labels.append(labels(2)); positions.append(buttons.indexOf(labels(2))); pressButton(buttons.indexOf(labels(2)))
      }
    }
  }

  private def state(): (Int, List[Int]) = {
    interactions.moveAway()
    val displayMat = interactions.screenshotOnModule(Rect(72, 55, 85, 67))
    val buttons = interactions.screenshotOnModule(Rect(30, 173, 172, 70))
    val buttonMat1 = buttons.submat(Rect(0, 0, 40, 70))
    val buttonMat2 = buttons.submat(Rect(44, 0, 40, 70))
    val buttonMat3 = buttons.submat(Rect(88, 0, 40, 70))
    val buttonMat4 = buttons.submat(Rect(132, 0, 40, 70))

    val display = MatchTemplate.bestMatch(displayMat, Seq(
      (Memory.Display1, 1),
      (Memory.Display2, 2),
      (Memory.Display3, 3),
      (Memory.Display4, 4)
    ).iterator)._2

    val button1 = matchButton(buttonMat1)
    val button2 = matchButton(buttonMat2)
    val button3 = matchButton(buttonMat3)
    val button4 = matchButton(buttonMat4)

    (display, List(button1, button2, button3, button4))
  }

  private def matchButton(mat: Mat): Int =
    MatchTemplate.bestMatch(mat, Seq(
      (Memory.Button1, 1),
      (Memory.Button2, 2),
      (Memory.Button3, 3),
      (Memory.Button4, 4)
    ).iterator)._2

  private def pressButton(i: Int): Unit = {
    interactions.pressOnModule(Point(50 + 44 * i, 208))
  }

object Memory:
  private val Button1: Mat = Imgcodecs.imread("images/memory/button1.bmp")
  private val Button2: Mat = Imgcodecs.imread("images/memory/button2.bmp")
  private val Button3: Mat = Imgcodecs.imread("images/memory/button3.bmp")
  private val Button4: Mat = Imgcodecs.imread("images/memory/button4.bmp")
  private val Display1: Mat = Imgcodecs.imread("images/memory/display1.bmp")
  private val Display2: Mat = Imgcodecs.imread("images/memory/display2.bmp")
  private val Display3: Mat = Imgcodecs.imread("images/memory/display3.bmp")
  private val Display4: Mat = Imgcodecs.imread("images/memory/display4.bmp")
