package com.zeramorphic.ktane
package modules

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

import scala.collection.mutable.ArrayBuffer

class Maze(interactions: Interactions):
  def solve(): Unit = {
    val maze = interactions.screenshotOnModule(Rect(48, 67, 162, 162))

    // Identify the green circles.
    val green = Mat()
    Core.extractChannel(maze, green, 1)
    val circles = Mat()
    Imgproc.HoughCircles(green, circles, Imgproc.HOUGH_GRADIENT, 1.0, 20, 200, 10, 6, 20)

    if circles.cols != 2 then
      throw Exception("expected exactly two green circles: " + circles.cols)

    val data: Array[Float] = Array(0f, 0f, 0f)
    circles.get(0, 0, data)
    val circle1X = ((data(0) - 17) / 26).round
    val circle1Y = ((data(1) - 17) / 26).round
    circles.get(0, 1, data)
    val circle2X = ((data(0) - 17) / 26).round
    val circle2Y = ((data(1) - 17) / 26).round

    // The white dot has the highest blue value.
    val blue = Mat()
    Core.extractChannel(maze, blue, 0)
    val blueMinMax = Core.minMaxLoc(blue)
    val posX = ((blueMinMax.maxLoc.x - 17) / 26).round.toInt
    val posY = ((blueMinMax.maxLoc.y - 17) / 26).round.toInt

    // Identify the red target by subtracting the green channel from red.
    val red = Mat()
    Core.extractChannel(maze, red, 2)
    Core.subtract(red, green, red)
    Imgproc.erode(red, red, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, Size(6, 6)))
    val redMinMax = Core.minMaxLoc(red)
    val targetX = ((redMinMax.maxLoc.x - 17) / 26).round.toInt
    val targetY = ((redMinMax.maxLoc.y - 17) / 26).round.toInt

    def right(x: Int, y: Int): (Int, Int, Int, Int) = (x, y, x + 1, y)

    def below(x: Int, y: Int): (Int, Int, Int, Int) = (x, y, x, y + 1)

    // If a pair of positions (x1, y1, x2, y2) is in this list,
    // one cannot move between them.
    // These are the mazes listed in reading order in the bomb manual.
    // The walls are described as to the right of, or below, a given grid cell, sorted by column.
    // This ensures each wall is describable in a unique way.
    val walls: List[(Int, Int, Int, Int)] = (circle1X, circle1Y, circle2X, circle2Y) match {
      case (0, 1, 5, 2) | (5, 2, 0, 1) => List(
        right(0, 1), right(0, 2), right(0, 3),
        below(1, 0), below(1, 2), below(1, 3), below(1, 4), right(1, 5),
        right(2, 0), right(2, 1), below(2, 1), right(2, 2), below(2, 3), right(2, 4),
        below(3, 1), right(3, 3), below(3, 3), right(3, 5),
        below(4, 0), below(4, 1), below(4, 2), below(4, 3), right(4, 4), below(4, 4),
        below(5, 0),
      )
      case (1, 3, 4, 1) | (4, 1, 1, 3) => List(
        below(0, 0), right(0, 2), right(0, 4), right(0, 5),
        right(1, 1), below(1, 1), right(1, 3), below(1, 3), right(1, 4),
        right(2, 0), below(2, 0), right(2, 2), below(2, 2), right(2, 4), right(2, 5),
        right(3, 1), below(3, 1), right(3, 3), below(3, 3),
        below(4, 1), below(4, 2), right(4, 3), right(4, 4), below(4, 4),
        below(5, 0)
      )
      case (3, 3, 5, 3) | (5, 3, 3, 3) => List(
        right(0, 1), below(0, 1), right(0, 3), right(0, 4),
        below(1, 0), right(1, 1), right(1, 2), right(1, 3), below(1, 4),
        right(2, 0), right(2, 1), right(2, 2), right(2, 3), right(2, 4), below(2, 4),
        right(3, 0), below(3, 1), right(3, 3), right(3, 4), right(3, 5),
        right(4, 1), below(4, 1), right(4, 2), right(4, 3), right(4, 4),
      )
      case (0, 0, 0, 3) | (0, 3, 0, 0) => List(
        right(0, 1), right(0, 2), right(0, 3),
        right(1, 0), right(1, 1), below(1, 2), below(1, 3), below(1, 4),
        below(2, 0), right(2, 2), below(2, 2), below(2, 3), below(2, 4),
        below(3, 0), below(3, 1), below(3, 3), below(3, 4),
        below(4, 0), below(4, 1), right(4, 2), below(4, 2), below(4, 3), right(4, 4), right(4, 5),
      )
      case (3, 5, 4, 2) | (4, 2, 3, 5) => List(
        below(0, 0), right(0, 3), right(0, 4), right(0, 5),
        below(1, 0), below(1, 1), right(1, 2), below(1, 3),
        below(2, 0), below(2, 1), below(2, 2), below(2, 3), below(2, 4),
        below(3, 0), right(3, 2), below(3, 2), right(3, 3), below(3, 4),
        right(4, 1), below(4, 1), right(4, 3), below(4, 3), right(4, 4), below(4, 4),
        below(5, 1),
      )
      case (2, 4, 4, 0) | (4, 0, 2, 4) => List(
        right(0, 0), right(0, 1), below(0, 3),
        right(1, 1), right(1, 2), below(1, 2), right(1, 3), right(1, 4), below(1, 4),
        right(2, 0), right(2, 1), right(2, 2), below(2, 2), right(2, 4), below(2, 4),
        below(3, 0), right(3, 2), right(3, 3), right(3, 4), right(3, 5),
        right(4, 1), below(4, 1), right(4, 3), below(4, 4),
        below(5, 2),
      )
      case (1, 0, 1, 5) | (1, 5, 1, 0) => List(
        right(0, 1), below(0, 2), right(0, 4),
        below(1, 0), right(1, 2), below(1, 2), right(1, 3), right(1, 4), below(1, 4),
        below(2, 0), right(2, 1), below(2, 1), below(2, 4),
        right(3, 0), below(3, 1), right(3, 2), below(3, 2), below(3, 3), below(3, 4),
        right(4, 1), below(4, 1), right(4, 3), below(4, 3), right(4, 4),
        below(5, 2),
      )
      case (2, 3, 3, 0) | (3, 0, 2, 3) => List(
        right(0, 0), right(0, 2), right(0, 3), right(0, 4),
        below(1, 1), below(1, 3), right(1, 4),
        below(2, 0), right(2, 1), below(2, 1), below(2, 2), right(2, 3), below(2, 4),
        right(3, 0), below(3, 1), below(3, 2), below(3, 3), below(3, 4),
        right(4, 1), below(4, 1), right(4, 2), below(4, 3), below(4, 4),
        below(5, 3), below(5, 4)
      )
      case (0, 4, 2, 1) | (2, 1, 0, 4) => List(
        right(0, 0), right(0, 1), right(0, 3), right(0, 4),
        right(1, 1), below(1, 2), right(1, 3), right(1, 4), right(1, 5),
        below(2, 0), right(2, 2), below(2, 2), right(2, 4),
        below(3, 0), right(3, 1), below(3, 1), right(3, 3), below(3, 3), right(3, 5),
        right(4, 1), right(4, 2), below(4, 2), below(4, 3), right(4, 4),
        below(5, 4)
      )
    }

    enum Direction:
      case U
      case L
      case D
      case R

      def opposite(): Direction = this match {
        case U => Direction.D
        case D => Direction.U
        case L => Direction.R
        case R => Direction.L
      }

    def neighbours(x: Int, y: Int): List[(Direction, Int, Int)] =
      List((Direction.R, x + 1, y), (Direction.D, x, y + 1), (Direction.L, x - 1, y), (Direction.U, x, y - 1))
        .filter((_, x2, y2) => 0 <= x2 && x2 <= 5 && 0 <= y2 && y2 <= 5)
        .filter((_, x2, y2) => !walls.contains((x, y, x2, y2)))
        .filter((_, x2, y2) => !walls.contains((x2, y2, x, y)))

    // Run a flood fill algorithm.
    // Since all distances between nodes are the same, Dijkstra's algorithm would probably be overcomplicating
    // the solution.
    val distances = (0 to 5).map(_ => (0 to 5).map(_ => Int.MaxValue).toArray).toArray
    distances(posX)(posY) = 0
    var search = ArrayBuffer((posX, posY))
    var currentDistance = 0

    while distances(targetX)(targetY) == Int.MaxValue do {
      val searchNow = search
      search = ArrayBuffer()
      for (x, y) <- searchNow do {
        distances(x)(y) = currentDistance
        search.appendAll(neighbours(x, y)
          .map((_, x, y) => (x, y))
          .filter((x, y) => distances(x)(y) == Int.MaxValue))
      }
      currentDistance += 1
    }

    // Reconstruct a path to the target by going in reverse.
    val path = ArrayBuffer[Direction]()
    var currentX = targetX
    var currentY = targetY
    while currentX != posX || currentY != posY do {
      val (direction, newX, newY) = neighbours(currentX, currentY)
        .find((_, x, y) => distances(x)(y) == distances(currentX)(currentY) - 1)
        .get
      currentX = newX
      currentY = newY
      path.prepend(direction.opposite())
    }

    for direction <- path do {
      direction match {
        case Direction.U => interactions.pressOnModule(Point(133, 34))
        case Direction.L => interactions.pressOnModule(Point(17, 143))
        case Direction.D => interactions.pressOnModule(Point(133, 261))
        case Direction.R => interactions.pressOnModule(Point(245, 143))
      }
      Thread.sleep(100)
    }
  }
