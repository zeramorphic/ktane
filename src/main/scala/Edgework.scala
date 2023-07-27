package com.zeramorphic.ktane

case class Edgework(batteriesD: Int,
                    batteriesAA: Int,
                    lit: List[Indicator],
                    unlit: List[Indicator],
                    plates: List[List[Port]],
                    serial: String):

  override def toString: String = {
    val sb = StringBuilder()
    sb.append("EDGEWORK:\n")
    sb.append(batteries).append(" in ").append(batteryHolders).append("\n")
    for indicator <- lit do sb.append("lit ").append(indicator).append("\n")
    for indicator <- unlit do sb.append("unlit ").append(indicator).append("\n")
    for plate <- plates do {
      sb.append("plate:")
      for port <- plate do sb.append(" ").append(port)
      sb.append("\n")
    }
    sb.append("serial # ").append(serial)
    sb.toString
  }

  def batteries: Int = batteriesD + batteriesAA * 2

  def batteryHolders: Int = batteriesD + batteriesAA
