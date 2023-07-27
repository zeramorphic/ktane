package com.zeramorphic.ktane

import net.sourceforge.tess4j.Tesseract
import org.opencv.core.*

import scala.collection.mutable.ArrayBuffer

class ReadEdgework(interactions: Interactions):
  private val screenshot: Mat = interactions.screenshot(Rect(0, 9, 1605, 68))
  ImageConversion.writeToFile(screenshot, "edgework")

  def read(): Unit = {
    val tesseract = Tesseract()
    tesseract.setDatapath("tessdata")
    tesseract.setLanguage("eng")

    val batteriesD = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.BatteryD).length
    val batteriesAA = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.BatteryAA).length

    val lit = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.IndicatorLit, 0.7)
      .map(indicator => Indicator.fromString(tesseract.doOCR(ImageConversion.matToBufferedImage(screenshot.submat(
        indicator.y.toInt + 5, indicator.y.toInt + 37,
        indicator.x.toInt + 53, indicator.x.toInt + 120))).trim)
      )

    val unlit = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.IndicatorUnlit, 0.7)
      .map(indicator => Indicator.fromString(tesseract.doOCR(ImageConversion.matToBufferedImage(screenshot.submat(
        indicator.y.toInt + 5, indicator.y.toInt + 37,
        indicator.x.toInt + 53, indicator.x.toInt + 120))).trim)
      )

    val plateLeft = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.PlateLeft, 0.9)
    val plateRight = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.PlateRight, 0.9)

    if plateLeft.length != plateRight.length then
      throw Exception("plates misaligned: " +
        plateLeft.mkString("Array(", ", ", ")") + "; " +
        plateRight.mkString("Array(", ", ", ")"))

    val plates = plateLeft.map(_ => ArrayBuffer[Port]()).toList
    val possiblePorts = Seq(
      (Port.Dvi, ReadEdgeworkSymbols.PortDvi),
      (Port.Parallel, ReadEdgeworkSymbols.PortParallel),
      (Port.Ps2, ReadEdgeworkSymbols.PortPs2),
      (Port.Rca, ReadEdgeworkSymbols.PortRca),
      (Port.Rj45, ReadEdgeworkSymbols.PortRj45),
      (Port.Serial, ReadEdgeworkSymbols.PortSerial),
    )

    for (port, symbol) <- possiblePorts do {
      for occurrence <- MatchTemplate.occurrences(screenshot, symbol, 0.9) do {
        val index = plateLeft.indices.find(index =>
          plateLeft(index).x <= occurrence.x && occurrence.x <= plateRight(index).x).get
        plates(index).append(port)
      }
    }

    val serialNumber = MatchTemplate.occurrences(screenshot, ReadEdgeworkSymbols.SerialNumber)
    if serialNumber.length == 1 then {
      val submat = screenshot.submat(
        serialNumber(0).y.toInt + 30, serialNumber(0).y.toInt + 65,
        serialNumber(0).x.toInt - 15, serialNumber(0).x.toInt + 120)
      val ocr = tesseract.doOCR(ImageConversion.matToBufferedImage(submat)).trim
      ImageConversion.writeToFile(submat, "submatrix")

      val edgework = Edgework(batteriesD, batteriesAA, lit.toList, unlit.toList, plates.map(buf => buf.toList), ocr)
      println(edgework)
    }
  }
