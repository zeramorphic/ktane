package com.zeramorphic.ktane
package modules

import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

class Password(interactions: Interactions) extends OneshotModule:
  override def solve(): Unit = {
    val spinners = (0 to 4).map(_ => (0 to 5).map(_ => '?').toArray).toArray

    for i <- 0 to 5 do {
      val screenshot = isolateLetters(interactions.screenshotOnModule(Rect(37, 120, 201, 45)))
      for (j, x) <- Seq((0, 0), (1, 42), (2, 83), (3, 124), (4, 165)) do {
        val submat = screenshot.submat(Rect(x, 0, 36, 45))
        val (coeff, letterIndex) = MatchTemplate.bestMatch(submat, Password.Alphabet.iterator.zipWithIndex)
        spinners(j)(i) = ('A' + letterIndex).toChar
      }

      if i != 5 then {
        clickUpArrow(0)
        clickUpArrow(1)
        clickUpArrow(2)
        clickUpArrow(3)
        clickUpArrow(4)
        Thread.sleep(300)
      }
    }

    val words = List(
      "ABOUT", "AFTER", "AGAIN", "BELOW", "COULD",
      "EVERY", "FIRST", "FOUND", "GREAT", "HOUSE",
      "LARGE", "LEARN", "NEVER", "OTHER", "PLACE",
      "PLANT", "POINT", "RIGHT", "SMALL", "SOUND",
      "SPELL", "STILL", "STUDY", "THEIR", "THERE",
      "THESE", "THING", "THINK", "THREE", "WATER",
      "WHERE", "WHICH", "WORLD", "WOULD", "WRITE")

    val word = words.find(word => word.indices.forall(i => spinners(i).contains(word(i))))
    word match {
      case None => println("could not find word")
      case Some(word) =>
        for i <- 0 to 4 do {
          spinners(i).indexOf(word(i)) match {
            case 0 => clickUpArrow(i)
            case 1 => clickUpArrow(i); clickUpArrow(i)
            case 2 => clickUpArrow(i); clickUpArrow(i); clickUpArrow(i)
            case 3 => clickDownArrow(i); clickDownArrow(i)
            case 4 => clickDownArrow(i)
            case 5 =>
          }
        }
        interactions.pressOnModule(Point(150, 250))
    }
    None
  }

  /**
   * Converts the green colour of the password module to black and white for easier processing.
   */
  private def isolateLetters(mat: Mat): Mat = {
    // First extract the green channel.
    val channel = Mat()
    Core.extractChannel(mat, channel, 1)
    val dst = Mat()
    Imgproc.threshold(channel, dst, 128, 255, Imgproc.THRESH_BINARY)
    dst
  }

  private def clickUpArrow(button: Int): Unit = {
    interactions.pressOnModule(Point(55 + 41 * button, 70))
    Thread.sleep(50)
  }

  private def clickDownArrow(button: Int): Unit = {
    interactions.pressOnModule(Point(55 + 41 * button, 215))
    Thread.sleep(50)
  }

object Password:
  private val Alphabet: List[Mat] =
    ('A' to 'Z').map(letter => {
      val mat = Imgcodecs.imread(s"images/password/$letter.bmp")
      val channel = Mat()
      Core.extractChannel(mat, channel, 0)
      channel.submat(Rect(5, 5, channel.width - 10, channel.height - 10))
    }).toList
