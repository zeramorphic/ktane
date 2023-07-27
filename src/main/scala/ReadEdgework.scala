package com.zeramorphic.ktane

import org.opencv.core.*

class ReadEdgework(interactions: Interactions):
  private val screenshot: Mat = interactions.screenshot(Rect(0, 9, 1605, 68))
  ImageConversion.writeToFile(screenshot, "edgework")

  def read(): Unit = {
    val occurrencesD = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.BatteryD)
    println("D batteries: " + occurrencesD.mkString("Array(", ", ", ")"))

    val occurrencesAA = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.BatteryAA)
    println("AA batteries: " + occurrencesAA.mkString("Array(", ", ", ")"))

    val unlit = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.IndicatorUnlit, 0.7)
    println("unlit: " + unlit.mkString("Array(", ", ", ")"))

    val lit = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.IndicatorLit, 0.7)
    println("lit: " + lit.mkString("Array(", ", ", ")"))

    val plateLeft = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.PlateLeft, 0.9)
    println("plateLeft: " + plateLeft.mkString("Array(", ", ", ")"))

    val plateRight = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.PlateRight, 0.9)
    println("plateRight: " + plateRight.mkString("Array(", ", ", ")"))

    val serialNumber = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.SerialNumber)
    println("Serial number: " + serialNumber.mkString("Array(", ", ", ")"))
  }
