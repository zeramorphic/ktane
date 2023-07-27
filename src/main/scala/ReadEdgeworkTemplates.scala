package com.zeramorphic.ktane

import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs

object ReadEdgeworkTemplates:
  val BatteryAA: Mat = Imgcodecs.imread("images/edgework/battery_aa.png")
  val BatteryD: Mat = Imgcodecs.imread("images/edgework/battery_d.png")
  val IndicatorLit: Mat = Imgcodecs.imread("images/edgework/indicator_lit.png")
  val IndicatorUnlit: Mat = Imgcodecs.imread("images/edgework/indicator_unlit.png")
  val PlateLeft: Mat = Imgcodecs.imread("images/edgework/plate_left.png")
  val PlateRight: Mat = Imgcodecs.imread("images/edgework/plate_right.png")
  val SerialNumber: Mat = Imgcodecs.imread("images/edgework/serial_number.png")

  val PortDvi: Mat = Imgcodecs.imread("images/edgework/port_dvi.png")
  val PortParallel: Mat = Imgcodecs.imread("images/edgework/port_parallel.png")
  val PortPs2: Mat = Imgcodecs.imread("images/edgework/port_ps2.png")
  val PortRca: Mat = Imgcodecs.imread("images/edgework/port_rca.png")
  val PortRj45: Mat = Imgcodecs.imread("images/edgework/port_rj45.png")
  val PortSerial: Mat = Imgcodecs.imread("images/edgework/port_serial.png")
