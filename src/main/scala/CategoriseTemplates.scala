package com.zeramorphic.ktane

import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs

object CategoriseTemplates:
  val Button: Mat = Imgcodecs.imread("images/categorise/button.png")
  val ComplicatedWires: Mat = Imgcodecs.imread("images/categorise/complicated_wires.png")
  val Maze: Mat = Imgcodecs.imread("images/categorise/maze.png")
  val MemoryLeft: Mat = Imgcodecs.imread("images/categorise/memory_left.png")
  val MemoryRight: Mat = Imgcodecs.imread("images/categorise/memory_right.png")
  val Morse: Mat = Imgcodecs.imread("images/categorise/morse.png")
  val Password: Mat = Imgcodecs.imread("images/categorise/password.png")
  val SimonSays1: Mat = Imgcodecs.imread("images/categorise/simon_says_1.png")
  val SimonSays2: Mat = Imgcodecs.imread("images/categorise/simon_says_2.png")
  val SimonSays3: Mat = Imgcodecs.imread("images/categorise/simon_says_3.png")
  val SimonSays4: Mat = Imgcodecs.imread("images/categorise/simon_says_4.png")
  val Symbols: Mat = Imgcodecs.imread("images/categorise/symbols.png")
  val WhosOnFirstDisplay: Mat = Imgcodecs.imread("images/categorise/whos_on_first_display.png")
  val WhosOnFirstProgress: Mat = Imgcodecs.imread("images/categorise/whos_on_first_progress.png")
  val WireSequencesArrow: Mat = Imgcodecs.imread("images/categorise/wire_sequences_arrow.png")
  val WireSequencesProgress: Mat = Imgcodecs.imread("images/categorise/wire_sequences_progress.png")
  val WiresLeft: Mat = Imgcodecs.imread("images/categorise/wires_left.png")
  val WiresRight: Mat = Imgcodecs.imread("images/categorise/wires_right.png")
