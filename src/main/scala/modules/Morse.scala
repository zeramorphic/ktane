package com.zeramorphic.ktane
package modules

import org.opencv.core.{Point, Rect}

import java.time.{Duration, Instant}

class Morse(interactions: Interactions) extends OneshotModule:
  override def solve(): Unit = {
    // Wait for the light to switch off.
    while lightOn() do Thread.sleep(10)

    val read = StringBuilder()
    var solution: Option[Int] = None
    while solution.isEmpty do {
      val turnedOffAt = Instant.now()
      while !lightOn() do Thread.sleep(10)
      val turnedOnAt = Instant.now()
      val durationOff = Duration.between(turnedOffAt, turnedOnAt)

      if durationOff.toMillis > 1500 then read.append("//")
      else if durationOff.toMillis > 600 then read.append("/")
      solution = trySolve(read.toString)

      if solution.isEmpty then {
        while lightOn() do Thread.sleep(10)
        val durationOn = Duration.between(turnedOnAt, Instant.now())

        if durationOn.toMillis > 500 then read.append("-") else read.append(".")
        solution = trySolve(read.toString)
      }
    }

    for i <- 0 until solution.get do {
      interactions.pressOnModule(Point(230, 170))
      Thread.sleep(50)
    }
    interactions.pressOnModule(Point(145, 235))
  }

  def lightOn(): Boolean = {
    val screenshot = interactions.screenshotOnModule(Rect(100, 40, 1, 1))
    val data = Array[Byte](0, 0, 0)
    screenshot.get(0, 0, data)
    data.map(value => Math.floorMod(value.toInt, 255)).zip(Seq(71, 222, 251)).map((a, b) => (a - b).abs).sum < 50
  }

  /**
   * Returns the amount of times we need to press the right arrow before submitting.
   */
  def trySolve(partialRead: String): Option[Int] = {
    val words = List("shell", "halls", "slick", "trick", "boxes", "leaks", "strobe",
      "bistro", "flick", "bombs", "break", "brick", "steak", "sting", "vector", "beats")
    val validWords = words.zipWithIndex.filter((word, _) => {
      val morse = Morse.stringToMorse(word)
      (morse + "//" + morse).contains(partialRead)
    })
    if validWords.length == 1 then Some(validWords.head._2) else None
  }

object Morse:
  def stringToMorse(string: String): String = string.iterator.map(Morse.letterToMorse).mkString("/")

  def letterToMorse(char: Char): String = char.toUpper match {
    case 'A' => ".-"
    case 'B' => "-..."
    case 'C' => "-.-."
    case 'D' => "-.."
    case 'E' => "."
    case 'F' => "..-."
    case 'G' => "--."
    case 'H' => "...."
    case 'I' => ".."
    case 'J' => ".---"
    case 'K' => "-.-"
    case 'L' => ".-.."
    case 'M' => "--"
    case 'N' => "-."
    case 'O' => "---"
    case 'P' => ".--."
    case 'Q' => "--.-"
    case 'R' => ".-."
    case 'S' => "..."
    case 'T' => "-"
    case 'U' => "..-"
    case 'V' => "...-"
    case 'W' => ".--"
    case 'X' => "-..-"
    case 'Y' => "-.--"
    case 'Z' => "--.."
  }
