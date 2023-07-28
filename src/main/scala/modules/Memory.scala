package com.zeramorphic.ktane
package modules

import org.opencv.core.{Mat, Point, Rect}
import org.opencv.imgcodecs.Imgcodecs

import java.time.{Duration, Instant}
import scala.collection.mutable.ArrayBuffer

class Memory(interactions: Interactions) extends Module:
  private val labels = ArrayBuffer[Int]()
  private val positions = ArrayBuffer[Int]()

  override def solved: Boolean = labels.length == 5

  /**
   * Because of the lengthy animation times, we give this module a high priority.
   * This allows us to solve this module while others are also being solved.
   */
  override def priority: Int = 100

  override def attemptSolve(): Option[Instant] = {
    // Buttons are zero-indexed.
    // Labels are as written.
    labels.length match {
      case 0 =>
        val (display, buttons) = state()
        display match {
          case 1 | 2 => labels.append(buttons(1)); positions.append(1); pressButton(1)
          case 3 => labels.append(buttons(2)); positions.append(2); pressButton(2)
          case 4 => labels.append(buttons(3)); positions.append(3); pressButton(3)
        }
      case 1 =>
        val (display, buttons) = state()
        display match {
          case 1 => labels.append(4); positions.append(buttons.indexOf(4)); pressButton(buttons.indexOf(4))
          case 2 | 4 => labels.append(buttons(positions(0))); positions.append(positions(0)); pressButton(positions(0))
          case 3 => labels.append(buttons.head); positions.append(0); pressButton(0)
        }
      case 2 =>
        val (display, buttons) = state()
        display match {
          case 1 => labels.append(labels(1)); positions.append(buttons.indexOf(labels(1))); pressButton(buttons.indexOf(labels(1)))
          case 2 => labels.append(labels(0)); positions.append(buttons.indexOf(labels(0))); pressButton(buttons.indexOf(labels(0)))
          case 3 => labels.append(buttons(2)); positions.append(2); pressButton(2)
          case 4 => labels.append(4); positions.append(buttons.indexOf(4)); pressButton(buttons.indexOf(4))
        }
      case 3 =>
        val (display, buttons) = state()
        display match {
          case 1 => labels.append(buttons(positions(0))); positions.append(positions(0)); pressButton(positions(0))
          case 2 => labels.append(buttons.head); positions.append(0); pressButton(0)
          case 3 | 4 => labels.append(buttons(positions(1))); positions.append(positions(1)); pressButton(positions(1))
        }
      case 4 =>
        val (display, buttons) = state()
        display match {
          case 1 => labels.append(labels(0)); positions.append(buttons.indexOf(labels(0))); pressButton(buttons.indexOf(labels(0)))
          case 2 => labels.append(labels(1)); positions.append(buttons.indexOf(labels(1))); pressButton(buttons.indexOf(labels(1)))
          case 3 => labels.append(labels(3)); positions.append(buttons.indexOf(labels(3))); pressButton(buttons.indexOf(labels(3)))
          case 4 => labels.append(labels(2)); positions.append(buttons.indexOf(labels(2))); pressButton(buttons.indexOf(labels(2)))
        }
    }

    if labels.length == 5 then None else Some(Instant.now.plus(Duration.ofMillis(3300)))
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
