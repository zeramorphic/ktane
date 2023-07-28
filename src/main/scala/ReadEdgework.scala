package com.zeramorphic.ktane

import net.sourceforge.tess4j.Tesseract
import org.opencv.core.*

import scala.collection.mutable.ArrayBuffer

object ReadEdgework:
  def read(interactions: Interactions): Edgework = {
    val screenshot: Mat = interactions.screenshot(Rect(0, 9, 1605, 68))

    val tesseract = Tesseract()
    tesseract.setDatapath("tessdata_best")
    tesseract.setLanguage("eng")
    // Treat the image as a single word.
    tesseract.setPageSegMode(8)

    val batteriesD = MatchTemplate.occurrences(screenshot, ReadEdgeworkTemplates.BatteryD).length
    val batteriesAA = MatchTemplate.occurrences(screenshot, ReadEdgeworkTemplates.BatteryAA).length

    val lit = MatchTemplate.occurrences(screenshot, ReadEdgeworkTemplates.IndicatorLit, 0.8)
      .map(indicator => Indicator.fromString(tesseract.doOCR(ImageConversion.matToBufferedImage(screenshot.submat(
          indicator.y.toInt + 5, indicator.y.toInt + 37,
          indicator.x.toInt + 53, indicator.x.toInt + 120)))
        .replaceAll("[^A-Za-z]+", "")))

    val unlit = MatchTemplate.occurrences(screenshot, ReadEdgeworkTemplates.IndicatorUnlit, 0.6)
      .map(indicator => Indicator.fromString(tesseract.doOCR(ImageConversion.matToBufferedImage(screenshot.submat(
          indicator.y.toInt + 5, indicator.y.toInt + 37,
          indicator.x.toInt + 53, indicator.x.toInt + 120)))
        .replaceAll("[^A-Za-z]+", "")))

    val plateLeft = MatchTemplate.occurrences(screenshot, ReadEdgeworkTemplates.PlateLeft, 0.9)
    val plateRight = MatchTemplate.occurrences(screenshot, ReadEdgeworkTemplates.PlateRight, 0.9)

    if plateLeft.length != plateRight.length then
      throw Exception("plates misaligned: " +
        plateLeft.mkString("Array(", ", ", ")") + "; " +
        plateRight.mkString("Array(", ", ", ")"))

    val plates = plateLeft.map(_ => ArrayBuffer[Port]()).toList
    val possiblePorts = Seq(
      (Port.Dvi, ReadEdgeworkTemplates.PortDvi, 0.5),
      (Port.Parallel, ReadEdgeworkTemplates.PortParallel, 0.3),
      (Port.Ps2, ReadEdgeworkTemplates.PortPs2, 0.5),
      (Port.Rca, ReadEdgeworkTemplates.PortRca, 0.8),
      (Port.Rj45, ReadEdgeworkTemplates.PortRj45, 0.8),
      (Port.Serial, ReadEdgeworkTemplates.PortSerial, 0.5),
    )

    for (port, symbol, threshold) <- possiblePorts do {
      for occurrence <- MatchTemplate.occurrences(screenshot, symbol, threshold) do {
        val index = plateLeft.indices.find(index =>
          plateLeft(index).x <= occurrence.x && occurrence.x <= plateRight(index).x).get
        plates(index).append(port)
      }
    }

    val serialNumber = MatchTemplate.occurrences(screenshot, ReadEdgeworkTemplates.SerialNumber)
    if serialNumber.length != 1 then
      throw Exception("expected exactly one serial number: " + serialNumber.mkString("Array(", ", ", ")"))

    val submat = screenshot.submat(
      serialNumber(0).y.toInt + 30, serialNumber(0).y.toInt + 65,
      serialNumber(0).x.toInt - 15, serialNumber(0).x.toInt + 120)
    val ocr = tesseract.doOCR(ImageConversion.matToBufferedImage(submat)).trim

    Edgework(batteriesD, batteriesAA, lit.toList, unlit.toList, plates.map(buf => buf.toList), ocr)
  }
