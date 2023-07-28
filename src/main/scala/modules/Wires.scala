package com.zeramorphic.ktane
package modules

import org.opencv.core.Point
import org.opencv.imgproc.Imgproc

class Wires(interactions: Interactions, edgework: Edgework) extends OneshotModule:
  /**
   * Increase priority since this module can fail if the edgework read is bad.
   */
  override def priority: Int = 20

  override def solve(): Unit = {
    enum WireColour:
      case White
      case Yellow
      case Red
      case Blue
      case Black

    val screenshot = interactions.screenshotSelectedModule()
    Imgproc.cvtColor(screenshot, screenshot, Imgproc.COLOR_BGR2HSV)
    val data = Array[Byte](0, 0, 0)
    val wires = (0 to 5).map(row => {
      screenshot.get(35 + 40 * row, 65, data)
      val (hue, sat, value) = (data(0).toInt, Math.floorMod(data(1).toInt, 255), Math.floorMod(data(2).toInt, 255))
      if value < 10 then {
        // This is a black wire.
        Some(WireColour.Black)
      } else if value < 70 then {
        // This is an unconnected socket.
        None
      } else if sat > 100 then {
        // This is a coloured wire.
        val closestMatch = Seq((23, WireColour.Yellow), (9, WireColour.Red), (116, WireColour.Blue))
          .minBy((targetHue, _) => (targetHue - hue).abs.min((targetHue - hue + 180).abs))
        Some(closestMatch._2)
      } else {
        // This is a white wire.
        Some(WireColour.White)
      }
    }).toList

    val colours = wires.filter(col => col.nonEmpty).map(col => col.get)

    // Wires are zero-indexed.

    val cut = colours.length match {
      case 3 =>
        if !colours.contains(WireColour.Red) then 1
        else if colours.last == WireColour.White then 2
        else if colours.count(_ == WireColour.Blue) > 1 then
          colours.zipWithIndex.filter((col, _) => col == WireColour.Blue).last._2
        else 2
      case 4 =>
        if colours.count(_ == WireColour.Red) > 1 && edgework.lastDigit % 2 == 1 then
          colours.zipWithIndex.filter((col, _) => col == WireColour.Red).last._2
        else if colours.last == WireColour.Yellow && !colours.contains(WireColour.Red) then 0
        else if colours.count(_ == WireColour.Blue) == 1 then 0
        else if colours.count(_ == WireColour.Yellow) > 1 then 3
        else 1
      case 5 =>
        if colours.last == WireColour.Black && edgework.lastDigit % 2 == 1 then 3
        else if colours.count(_ == WireColour.Red) == 1 && colours.count(_ == WireColour.Yellow) > 1 then 0
        else if !colours.contains(WireColour.Black) then 1
        else 0
      case 6 =>
        if !colours.contains(WireColour.Yellow) && edgework.lastDigit % 2 == 1 then 2
        else if colours.count(_ == WireColour.Yellow) == 1 && colours.count(_ == WireColour.White) > 1 then 3
        else if !colours.contains(WireColour.Red) then 5
        else 3
    }

    val cutIndex = wires.iterator.zipWithIndex.filter((col, _) => col.nonEmpty).toList(cut)._2
    interactions.pressOnModule(Point(65, 35 + 40 * cutIndex))
  }
