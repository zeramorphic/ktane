package com.zeramorphic.ktane
package modules

import org.opencv.core.{Core, Mat, Point, Rect}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

import java.time.{Duration, Instant}

class WhosOnFirst(interactions: Interactions) extends Module:
  private var solvedStages: Int = 0

  override def solved: Boolean = solvedStages == 3

  /**
   * Because of the lengthy animation times, we give this module a high priority.
   * This allows us to solve this module while others are also being solved.
   */
  override def priority: Int = 100
  
  override def attemptSolve(): Option[Instant] = {
    val screenshot = interactions.screenshotSelectedModule()
    ImageConversion.writeToFile(screenshot, s"whos_on_first$solvedStages")

    val display = screenshot.submat(Rect(52, 40, 110, 30))
    val displayWord = MatchTemplate.bestMatch(display, WhosOnFirst.Displays.iterator)._2

    val words = (0 to 1)
      .map(i => (0 to 2)
        .map(j => {
          val submat = screenshot.submat(Rect(43 + 84 * i, 108 + 49 * j, 65, 30))
          // Attempt to remove the background.
          Core.extractChannel(submat, submat, 2)
          Imgproc.threshold(submat, submat, 200, 255, Imgproc.THRESH_TRUNC)
          MatchTemplate.bestMatch(submat, WhosOnFirst.Buttons.iterator)._2
        })
        .toList)
      .toList

    val keyword = displayWord match {
      case "UR" => words(0)(0)
      case "YES" | "NOTHING" | "LED" | "THEY ARE" => words(0)(1)
      case "" | "REED" | "LEED" | "THEY'RE" => words(0)(2)
      case "FIRST" | "OKAY" | "C" => words(1)(0)
      case "BLANK" | "READ" | "RED" | "YOU" | "YOUR" | "YOU'RE" | "THEIR" => words(1)(1)
      case "DISPLAY" | "SAYS" | "NO" | "LEAD" | "HOLD ON" | "YOU ARE" | "THERE" | "SEE" | "CEE" => words(1)(2)
    }

    val wordSequence = keyword match {
      case "READY" => Seq("YES", "OKAY", "WHAT", "MIDDLE", "LEFT", "PRESS", "RIGHT", "BLANK", "READY", "NO", "FIRST", "UHHH", "NOTHING", "WAIT")
      case "FIRST" => Seq("LEFT", "OKAY", "YES", "MIDDLE", "NO", "RIGHT", "NOTHING", "UHHH", "WAIT", "READY", "BLANK", "WHAT", "PRESS", "FIRST")
      case "NO" => Seq("BLANK", "UHHH", "WAIT", "FIRST", "WHAT", "READY", "RIGHT", "YES", "NOTHING", "LEFT", "PRESS", "OKAY", "NO", "MIDDLE")
      case "BLANK" => Seq("WAIT", "RIGHT", "OKAY", "MIDDLE", "BLANK", "PRESS", "READY", "NOTHING", "NO", "WHAT", "LEFT", "UHHH", "YES", "FIRST")
      case "NOTHING" => Seq("UHHH", "RIGHT", "OKAY", "MIDDLE", "YES", "BLANK", "NO", "PRESS", "LEFT", "WHAT", "WAIT", "FIRST", "NOTHING", "READY")
      case "YES" => Seq("OKAY", "RIGHT", "UHHH", "MIDDLE", "FIRST", "WHAT", "PRESS", "READY", "NOTHING", "YES", "LEFT", "BLANK", "NO", "WAIT")
      case "WHAT" => Seq("UHHH", "WHAT", "LEFT", "NOTHING", "READY", "BLANK", "MIDDLE", "NO", "OKAY", "FIRST", "WAIT", "YES", "PRESS", "RIGHT")
      case "UHHH" => Seq("READY", "NOTHING", "LEFT", "WHAT", "OKAY", "YES", "RIGHT", "NO", "PRESS", "BLANK", "UHHH", "MIDDLE", "WAIT", "FIRST")
      case "LEFT" => Seq("RIGHT", "LEFT", "FIRST", "NO", "MIDDLE", "YES", "BLANK", "WHAT", "UHHH", "WAIT", "PRESS", "READY", "OKAY", "NOTHING")
      case "RIGHT" => Seq("YES", "NOTHING", "READY", "PRESS", "NO", "WAIT", "WHAT", "RIGHT", "MIDDLE", "LEFT", "UHHH", "BLANK", "OKAY", "FIRST")
      case "MIDDLE" => Seq("BLANK", "READY", "OKAY", "WHAT", "NOTHING", "PRESS", "NO", "WAIT", "LEFT", "MIDDLE", "RIGHT", "FIRST", "UHHH", "YES")
      case "OKAY" => Seq("MIDDLE", "NO", "FIRST", "YES", "UHHH", "NOTHING", "WAIT", "OKAY", "LEFT", "READY", "BLANK", "PRESS", "WHAT", "RIGHT")
      case "WAIT" => Seq("UHHH", "NO", "BLANK", "OKAY", "YES", "LEFT", "FIRST", "PRESS", "WHAT", "WAIT", "NOTHING", "READY", "RIGHT", "MIDDLE")
      case "PRESS" => Seq("RIGHT", "MIDDLE", "YES", "READY", "PRESS", "OKAY", "NOTHING", "UHHH", "BLANK", "LEFT", "FIRST", "WHAT", "NO", "WAIT")
      case "YOU" => Seq("SURE", "YOU ARE", "YOUR", "YOU'RE", "NEXT", "UH HUH", "UR", "HOLD", "WHAT?", "YOU", "UH UH", "LIKE", "DONE", "U")
      case "YOU ARE" => Seq("YOUR", "NEXT", "LIKE", "UH HUH", "WHAT?", "DONE", "UH UH", "HOLD", "YOU", "U", "YOU'RE", "SURE", "UR", "YOU ARE")
      case "YOUR" => Seq("UH UH", "YOU ARE", "UH HUH", "YOUR", "NEXT", "UR", "SURE", "U", "YOU'RE", "YOU", "WHAT?", "HOLD", "LIKE", "DONE")
      case "YOU'RE" => Seq("YOU", "YOU'RE", "UR", "NEXT", "UH UH", "YOU ARE", "U", "YOUR", "WHAT?", "UH HUH", "SURE", "DONE", "LIKE", "HOLD")
      case "UR" => Seq("DONE", "U", "UR", "UH HUH", "WHAT?", "SURE", "YOUR", "HOLD", "YOU'RE", "LIKE", "NEXT", "UH UH", "YOU ARE", "YOU")
      case "U" => Seq("UH HUH", "SURE", "NEXT", "WHAT?", "YOU'RE", "UR", "UH UH", "DONE", "U", "YOU", "LIKE", "HOLD", "YOU ARE", "YOUR")
      case "UH HUH" => Seq("UH HUH", "YOUR", "YOU ARE", "YOU", "DONE", "HOLD", "UH UH", "NEXT", "SURE", "LIKE", "YOU'RE", "UR", "U", "WHAT?")
      case "UH UH" => Seq("UR", "U", "YOU ARE", "YOU'RE", "NEXT", "UH UH", "DONE", "YOU", "UH HUH", "LIKE", "YOUR", "SURE", "HOLD", "WHAT?")
      case "WHAT?" => Seq("YOU", "HOLD", "YOU'RE", "YOUR", "U", "DONE", "UH UH", "LIKE", "YOU ARE", "UH HUH", "UR", "NEXT", "WHAT?", "SURE")
      case "DONE" => Seq("SURE", "UH HUH", "NEXT", "WHAT?", "YOUR", "UR", "YOU'RE", "HOLD", "LIKE", "YOU", "U", "YOU ARE", "UH UH", "DONE")
      case "NEXT" => Seq("WHAT?", "UH HUH", "UH UH", "YOUR", "HOLD", "SURE", "NEXT", "LIKE", "DONE", "YOU ARE", "UR", "YOU'RE", "U", "YOU")
      case "HOLD" => Seq("YOU ARE", "U", "DONE", "UH UH", "YOU", "UR", "SURE", "WHAT?", "YOU'RE", "NEXT", "HOLD", "UH HUH", "YOUR", "LIKE")
      case "SURE" => Seq("YOU ARE", "DONE", "LIKE", "YOU'RE", "YOU", "HOLD", "UH HUH", "UR", "SURE", "U", "WHAT?", "NEXT", "YOUR", "UH UH")
      case "LIKE" => Seq("YOU'RE", "NEXT", "U", "UR", "HOLD", "DONE", "UH UH", "WHAT?", "UH HUH", "YOU", "LIKE", "SURE", "YOU ARE", "YOUR")
    }

    val targetWord = wordSequence.find(word => words.exists(list => list.contains(word))).get
    val column = words.indexWhere(list => list.contains(targetWord))
    val row = words(column).indexOf(targetWord)

    interactions.pressOnModule(Point(75 + 85 * column, 125 + 48 * row))

    solvedStages += 1
    if solvedStages < 3 then Some(Instant.now.plus(Duration.ofMillis(3300))) else None
  }

object WhosOnFirst:
  private val Buttons: List[(Mat, String)] = List(
    (Imgcodecs.imread("images/whos_on_first/button/blank.png"), "BLANK"),
    (Imgcodecs.imread("images/whos_on_first/button/done.png"), "DONE"),
    (Imgcodecs.imread("images/whos_on_first/button/first.png"), "FIRST"),
    (Imgcodecs.imread("images/whos_on_first/button/hold.png"), "HOLD"),
    (Imgcodecs.imread("images/whos_on_first/button/left.png"), "LEFT"),
    (Imgcodecs.imread("images/whos_on_first/button/like.png"), "LIKE"),
    (Imgcodecs.imread("images/whos_on_first/button/middle.png"), "MIDDLE"),
    (Imgcodecs.imread("images/whos_on_first/button/next.png"), "NEXT"),
    (Imgcodecs.imread("images/whos_on_first/button/no.png"), "NO"),
    (Imgcodecs.imread("images/whos_on_first/button/nothing.png"), "NOTHING"),
    (Imgcodecs.imread("images/whos_on_first/button/okay.png"), "OKAY"),
    (Imgcodecs.imread("images/whos_on_first/button/press.png"), "PRESS"),
    (Imgcodecs.imread("images/whos_on_first/button/ready.png"), "READY"),
    (Imgcodecs.imread("images/whos_on_first/button/right.png"), "RIGHT"),
    (Imgcodecs.imread("images/whos_on_first/button/sure.png"), "SURE"),
    (Imgcodecs.imread("images/whos_on_first/button/u.png"), "U"),
    (Imgcodecs.imread("images/whos_on_first/button/uhhh.png"), "UHHH"),
    (Imgcodecs.imread("images/whos_on_first/button/uhhuh.png"), "UH HUH"),
    (Imgcodecs.imread("images/whos_on_first/button/uhuh.png"), "UH UH"),
    (Imgcodecs.imread("images/whos_on_first/button/ur.png"), "UR"),
    (Imgcodecs.imread("images/whos_on_first/button/wait.png"), "WAIT"),
    (Imgcodecs.imread("images/whos_on_first/button/what.png"), "WHAT"),
    (Imgcodecs.imread("images/whos_on_first/button/whatQuestion.png"), "WHAT?"),
    (Imgcodecs.imread("images/whos_on_first/button/yes.png"), "YES"),
    (Imgcodecs.imread("images/whos_on_first/button/you.png"), "YOU"),
    (Imgcodecs.imread("images/whos_on_first/button/youare.png"), "YOU ARE"),
    (Imgcodecs.imread("images/whos_on_first/button/your.png"), "YOUR"),
    (Imgcodecs.imread("images/whos_on_first/button/youre.png"), "YOU'RE"),
  ).map((image, word) => {
    Core.extractChannel(image, image, 0)
    (image, word)
  })

  private val Displays: List[(Mat, String)] = List(
    (Imgcodecs.imread("images/whos_on_first/display/blank.png"), "BLANK"),
    (Imgcodecs.imread("images/whos_on_first/display/c.png"), "C"),
    (Imgcodecs.imread("images/whos_on_first/display/cee.png"), "CEE"),
    (Imgcodecs.imread("images/whos_on_first/display/display.png"), "DISPLAY"),
    (Imgcodecs.imread("images/whos_on_first/display/first.png"), "FIRST"),
    (Imgcodecs.imread("images/whos_on_first/display/holdon.png"), "HOLD ON"),
    (Imgcodecs.imread("images/whos_on_first/display/lead.png"), "LEAD"),
    (Imgcodecs.imread("images/whos_on_first/display/led.png"), "LED"),
    (Imgcodecs.imread("images/whos_on_first/display/leed.png"), "LEED"),
    (Imgcodecs.imread("images/whos_on_first/display/literallyBlank.png"), ""),
    (Imgcodecs.imread("images/whos_on_first/display/no.png"), "NO"),
    (Imgcodecs.imread("images/whos_on_first/display/nothing.png"), "NOTHING"),
    (Imgcodecs.imread("images/whos_on_first/display/okay.png"), "OKAY"),
    (Imgcodecs.imread("images/whos_on_first/display/read.png"), "READ"),
    (Imgcodecs.imread("images/whos_on_first/display/red.png"), "RED"),
    (Imgcodecs.imread("images/whos_on_first/display/reed.png"), "REED"),
    (Imgcodecs.imread("images/whos_on_first/display/says.png"), "SAYS"),
    (Imgcodecs.imread("images/whos_on_first/display/see.png"), "SEE"),
    (Imgcodecs.imread("images/whos_on_first/display/their.png"), "THEIR"),
    (Imgcodecs.imread("images/whos_on_first/display/there.png"), "THERE"),
    (Imgcodecs.imread("images/whos_on_first/display/theyare.png"), "THEY ARE"),
    (Imgcodecs.imread("images/whos_on_first/display/theyre.png"), "THEY'RE"),
    (Imgcodecs.imread("images/whos_on_first/display/ur.png"), "UR"),
    (Imgcodecs.imread("images/whos_on_first/display/yes.png"), "YES"),
    (Imgcodecs.imread("images/whos_on_first/display/you.png"), "YOU"),
    (Imgcodecs.imread("images/whos_on_first/display/youare.png"), "YOU ARE"),
    (Imgcodecs.imread("images/whos_on_first/display/your.png"), "YOUR"),
    (Imgcodecs.imread("images/whos_on_first/display/youre.png"), "YOU'RE"),
  )
