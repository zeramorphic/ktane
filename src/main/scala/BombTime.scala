package com.zeramorphic.ktane

import org.opencv.core.Rect

import scala.collection.mutable.ArrayBuffer

case class BombTime(hours: Int, minutes: Int, seconds: Int):
  override def toString: String =
    if hours > 0 then f"$hours:$minutes%02d:$seconds%02d"
    else if minutes > 0 then f"$minutes%02d:$seconds%02d"
    else f"$seconds%02d"

object BombTime:
  /**
   * Reads the in-game KTaNE timer.
   * This requires the Bomb HUD from the Tweaks mod.
   * This function is hopefully very fast, and doesn't use any image processing functions.
   */
  def read(interactions: Interactions): BombTime = {
    val timer = interactions.screenshot(Rect(1605, 76, 315, 88))
    //    ImageConversion.writeToFile(timer, "bombtime")

    val stride = 3
    val previousStates = ArrayBuffer[List[Boolean]]()
    // Whether we are checking the vertical segments on the 7-segment display or the horizontal ones.
    var vertical = true
    val string = StringBuilder()

    for x <- 0 until timer.width by stride do {
      // Sample some points on this column.
      val data = Array[Byte](0, 0, 0)

      // The top pixel, covered only by the upper horizontal line on the 7-segment display.
      timer.get(0, x, data)
      val top = !vertical && (data sameElements Array(0, 0, -1))

      // A pixel covered by the upper vertical lines on the 7-segment display
      // as well as the lights of the colon separating minutes and seconds.
      // Not covered by the upper horizontal line on the 7-segment display.
      timer.get(timer.height / 5, x, data)
      val high = data sameElements Array(0, 0, -1)

      // A pixel covered by the upper vertical lines on the 7-segment display
      // but not the lights of the colon.
      timer.get(timer.height / 4, x, data)
      val midHigh = data sameElements Array(0, 0, -1)

      // A pixel covered only by the middle horizontal line on the 7-segment display.
      timer.get(timer.height / 2, x, data)
      val mid = !vertical && (data sameElements Array(0, 0, -1))

      // The others are symmetric equivalents.

      timer.get(timer.height - timer.height / 4, x, data)
      val midLow = data sameElements Array(0, 0, -1)

      timer.get(timer.height - timer.height / 5, x, data)
      val low = data sameElements Array(0, 0, -1)

      timer.get(timer.height - 1, x, data)
      val bottom = !vertical && (data sameElements Array(0, 0, -1))

      val state = List(top, high, midHigh, mid, midLow, low, bottom)

      // By detecting the transitions between states as we scan from left to right, we can read the text on the timer.

      if vertical && state == List(false, false, false, false, false, false, false) then {
        // We're now looking for horizontal things.
        vertical = false
      } else if !vertical && (high || midHigh || midLow || low) then {
        // We're now looking for vertical things.
        vertical = true
      } else if previousStates.lastOption.contains(state) then {
        // No-op.
      } else if state == List(false, false, false, false, false, false, false) && previousStates.nonEmpty then {
        // If the state is blank, we should have just finished reading a character.
        // The digits in code are legible by flipping the grid of false and true values across the main diagonal.
        val char = previousStates match {
          case ArrayBuffer(
          List(false, true, true, false, true, true, false),
          List(true, false, false, false, false, false, true),
          List(false, true, true, false, true, true, false)) => '0'
          case ArrayBuffer(
          List(false, true, true, false, true, true, false)) => '1'
          case ArrayBuffer(
          List(false, false, false, false, true, true, false),
          List(true, false, false, true, false, false, true),
          List(false, true, true, false, false, false, false)) => '2'
          // Due to the way this method works, the character 3 can be observed with two different state patterns.
          case ArrayBuffer(
          List(true, false, false, true, false, false, true),
          List(false, true, true, false, true, true, false)) => '3'
          case ArrayBuffer(
          List(true, false, false, false, false, false, true),
          List(true, false, false, true, false, false, true),
          List(false, true, true, false, true, true, false)) => '3'
          case ArrayBuffer(
          List(false, true, true, false, false, false, false),
          List(false, false, false, true, false, false, false),
          List(false, true, true, false, true, true, false)) => '4'
          case ArrayBuffer(
          List(false, true, true, false, false, false, false),
          List(true, false, false, true, false, false, true),
          List(false, false, false, false, true, true, false)) => '5'
          case ArrayBuffer(
          List(false, true, true, false, true, true, false),
          List(true, false, false, true, false, false, true),
          List(false, false, false, false, true, true, false)) => '6'
          case ArrayBuffer(
          List(true, false, false, false, false, false, false),
          List(false, true, true, false, true, true, false)) => '7'
          case ArrayBuffer(
          List(false, true, true, false, true, true, false),
          List(true, false, false, true, false, false, true),
          List(false, true, true, false, true, true, false)) => '8'
          case ArrayBuffer(
          List(false, true, true, false, false, false, false),
          List(true, false, false, true, false, false, true),
          List(false, true, true, false, true, true, false)) => '9'
          case ArrayBuffer(List(false, true, false, false, false, true, false)) => ':'
          case ArrayBuffer(List(false, false, false, false, false, false, true)) => '.'
        }
        string.append(char)
        previousStates.clear()
      } else if state != List(false, false, false, false, false, false, false) then {
        // Append to the list of previous states.
        previousStates.append(state)
      }
    }

    // Ignore hundredths; we can't move fast enough for hundredths to be useful.
    val time = string.toString.split('.')(0)
    val components = time.split(':').map(component => component.toInt).toList.reverse
    BombTime(
      if components.length >= 3 then components(2) else 0,
      if components.length >= 2 then components(1) else 0,
      components.head
    )
  }

  /**
   * Waits until the given predicate about the bomb time is true.
   */
  def waitUntil(interactions: Interactions, predicate: BombTime => Boolean): Unit =
    while !predicate(BombTime.read(interactions)) do Thread.sleep(50)
