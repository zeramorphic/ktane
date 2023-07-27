package com.zeramorphic.ktane

enum Indicator:
  case Snd
  case Clr
  case Car
  case Ind
  case Frq
  case Sig
  case Nsa
  case Msa
  case Trn
  case Bob
  case Frk

  override def toString: String = this match {
    case Indicator.Snd => "SND"
    case Indicator.Clr => "CLR"
    case Indicator.Car => "CAR"
    case Indicator.Ind => "IND"
    case Indicator.Frq => "FRQ"
    case Indicator.Sig => "SIG"
    case Indicator.Nsa => "NSA"
    case Indicator.Msa => "MSA"
    case Indicator.Trn => "TRN"
    case Indicator.Bob => "BOB"
    case Indicator.Frk => "FRK"
  }

object Indicator {
  def fromString(name: String): Indicator = {
    name match {
      case "SND" => Indicator.Snd
      case "CLR" => Indicator.Clr
      case "CAR" => Indicator.Car
      case "IND" => Indicator.Ind
      case "FRQ" => Indicator.Frq
      case "SIG" => Indicator.Sig
      case "NSA" => Indicator.Nsa
      case "MSA" => Indicator.Msa
      case "TRN" => Indicator.Trn
      case "BOB" => Indicator.Bob
      case "FRK" => Indicator.Frk
      case _ => throw IllegalArgumentException(name + " was not a valid indicator")
    }
  }
}
