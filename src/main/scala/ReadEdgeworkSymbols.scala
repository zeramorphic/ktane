package com.zeramorphic.ktane

import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs

object ReadEdgeworkSymbols:
  val BatteryAA: Mat = Imgcodecs.imread("images/battery_aa.png")
  val BatteryD: Mat = Imgcodecs.imread("images/battery_d.png")
  val IndicatorLit: Mat = Imgcodecs.imread("images/indicator_lit.png")
  val IndicatorUnlit: Mat = Imgcodecs.imread("images/indicator_unlit.png")
  val PlateLeft: Mat = Imgcodecs.imread("images/plate_left.png")
  val PlateRight: Mat = Imgcodecs.imread("images/plate_right.png")
  val SerialNumber: Mat = Imgcodecs.imread("images/serial_number.png")
