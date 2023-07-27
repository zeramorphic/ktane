package com.zeramorphic.ktane

enum Port:
  case Dvi
  case Parallel
  case Ps2
  case Rca
  case Rj45
  case Serial

  override def toString: String = this match {
    case Dvi => "DVI-D"
    case Parallel => "Parallel"
    case Ps2 => "PS/2"
    case Rca => "RCA"
    case Rj45 => "RJ-45"
    case Serial => "Serial"
  }
