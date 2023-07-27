package com.zeramorphic.ktane
package modules

import modules.Symbol.*

import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs

class Keypad(interactions: Interactions):
  def solve(): Unit = {
    val screenshot = interactions.screenshotOnModule(Rect(32, 79, 179, 173))

    val options = Seq(
      (TemplateArchaicKoppa, ArchaicKoppa),
      (TemplateLittleYus, LittleYus),
      (TemplateLambda, Lambda),
      (TemplateModernKoppa, ModernKoppa),
      (TemplateBigYus, BigYus),
      (TemplateKai, Kai),
      (TemplateReversedDotted, ReversedDotted),
      (TemplateEWithDiaeresis, EWithDiaeresis),
      (TemplateHa, Ha),
      (TemplateWhiteStar, WhiteStar),
      (TemplateQuestion, Question),
      (TemplateCopyright, Copyright),
      (TemplateOrnateOmega, OrnateOmega),
      (TemplateZhe, Zhe),
      (TemplateDzje, Dzje),
      (TemplateBe, Be),
      (TemplatePilcrow, Pilcrow),
      (TemplateYat, Yat),
      (TemplateTeh, Teh),
      (TemplateDotted, Dotted),
      (TemplateKsi, Ksi),
      (TemplateBlackStar, BlackStar),
      (TemplateThousands, Thousands),
      (TemplateAe, Ae),
      (TemplatePsi, Psi),
      (TemplateShortI, ShortI),
      (TemplateOmega, Omega),
    )

    val symbol1 = MatchTemplate.bestMatch(screenshot.submat(Rect(0, 0, 80, 76)), options.iterator)._2
    val symbol2 = MatchTemplate.bestMatch(screenshot.submat(Rect(99, 0, 80, 76)), options.iterator)._2
    val symbol3 = MatchTemplate.bestMatch(screenshot.submat(Rect(0, 94, 80, 76)), options.iterator)._2
    val symbol4 = MatchTemplate.bestMatch(screenshot.submat(Rect(99, 94, 80, 76)), options.iterator)._2

    val columns = Seq(
      Seq(ArchaicKoppa, LittleYus, Lambda, ModernKoppa, BigYus, Kai, ReversedDotted),
      Seq(EWithDiaeresis, ArchaicKoppa, ReversedDotted, Ha, WhiteStar, Kai, Question),
      Seq(Copyright, OrnateOmega, Ha, Zhe, Dzje, Lambda, WhiteStar),
      Seq(Be, Pilcrow, Yat, BigYus, Zhe, Question, Teh),
      Seq(Psi, Teh, Yat, Dotted, Pilcrow, Ksi, BlackStar),
      Seq(Be, EWithDiaeresis, Thousands, Ae, Psi, ShortI, Omega))

    val column = columns.find(column => column.contains(symbol1) &&
      column.contains(symbol2) &&
      column.contains(symbol3) &&
      column.contains(symbol4))

    column match {
      case Some(column) =>
        for item <- column do {
          if item == symbol1 then {
            interactions.pressOnModule(Point(72, 117))
            Thread.sleep(100)
          } else if item == symbol2 then {
            interactions.pressOnModule(Point(171, 117))
            Thread.sleep(100)
          } else if item == symbol3 then {
            interactions.pressOnModule(Point(72, 211))
            Thread.sleep(100)
          } else if item == symbol4 then {
            interactions.pressOnModule(Point(171, 211))
            Thread.sleep(100)
          }
        }
      case None => println("could not find matching column")
    }
  }

private enum Symbol:
  /** Ϙ: Greek letter archaic koppa */
  case ArchaicKoppa
  /** Ѧ: Cyrillic capital letter little yus */
  case LittleYus
  /** ƛ: Latin small letter lambda with stroke */
  case Lambda
  /** Ϟ: Greek letter koppa */
  case ModernKoppa
  /** Ѭ: Cyrillic capital iotified big yus */
  case BigYus
  /** ϗ: Greek kai symbol */
  case Kai
  /** Ͽ: Greek capital reversed dotted lunate sigma symbol */
  case ReversedDotted
  /** Ӭ: Cyrillic capital E with diaeresis */
  case EWithDiaeresis
  /** Ҩ: Cyrillic capital letter Abkhasian ha */
  case Ha
  /** ☆: White star */
  case WhiteStar
  /** ¿: Inverted question mark */
  case Question
  /** ©: Copyright sign */
  case Copyright
  /** Ѽ: Ornate omega (Mislabelled in Unicode 5.1: <https://en.wikipedia.org/wiki/Omega_(Cyrillic)>) */
  case OrnateOmega
  /** Җ: Cyrillic capital letter zhe with descender */
  case Zhe
  /** Ԇ: Cyrillic capital letter Komi dzje */
  case Dzje
  /** б: Cyrillic small letter be */
  case Be
  /** ¶: Pilcrow sign */
  case Pilcrow
  /** Ѣ: Cyrillic capital letter yat */
  case Yat
  /** ټ: Arabic letter teh with ring */
  case Teh
  /** Ͼ: Greek capital dotted lunate sigma symbol */
  case Dotted
  /** Ѯ: Cyrillic capital letter ksi */
  case Ksi
  /** ★: Black star */
  case BlackStar
  /** ҂: Cyrillic thousands sign */
  case Thousands
  /** æ: Latin small letter ae */
  case Ae
  /** Ψ: Greek capital letter psi */
  case Psi
  /** Ҋ: Cyrillic capital letter short I with tail */
  case ShortI
  /** Ω: Greek capital letter omega */
  case Omega

private object Symbol:
  val TemplateArchaicKoppa: Mat = Imgcodecs.imread("images/keypad/archaicKoppa.bmp")
  val TemplateLittleYus: Mat = Imgcodecs.imread("images/keypad/littleYus.bmp")
  val TemplateLambda: Mat = Imgcodecs.imread("images/keypad/lambda.bmp")
  val TemplateModernKoppa: Mat = Imgcodecs.imread("images/keypad/modernKoppa.bmp")
  val TemplateBigYus: Mat = Imgcodecs.imread("images/keypad/bigYus.bmp")
  val TemplateKai: Mat = Imgcodecs.imread("images/keypad/kai.bmp")
  val TemplateReversedDotted: Mat = Imgcodecs.imread("images/keypad/reversedDotted.bmp")
  val TemplateEWithDiaeresis: Mat = Imgcodecs.imread("images/keypad/eWithDiaeresis.bmp")
  val TemplateHa: Mat = Imgcodecs.imread("images/keypad/ha.bmp")
  val TemplateWhiteStar: Mat = Imgcodecs.imread("images/keypad/whiteStar.bmp")
  val TemplateQuestion: Mat = Imgcodecs.imread("images/keypad/question.bmp")
  val TemplateCopyright: Mat = Imgcodecs.imread("images/keypad/copyright.bmp")
  val TemplateOrnateOmega: Mat = Imgcodecs.imread("images/keypad/ornateOmega.bmp")
  val TemplateZhe: Mat = Imgcodecs.imread("images/keypad/zhe.bmp")
  val TemplateDzje: Mat = Imgcodecs.imread("images/keypad/dzje.bmp")
  val TemplateBe: Mat = Imgcodecs.imread("images/keypad/be.bmp")
  val TemplatePilcrow: Mat = Imgcodecs.imread("images/keypad/pilcrow.bmp")
  val TemplateYat: Mat = Imgcodecs.imread("images/keypad/yat.bmp")
  val TemplateTeh: Mat = Imgcodecs.imread("images/keypad/teh.bmp")
  val TemplateDotted: Mat = Imgcodecs.imread("images/keypad/dotted.bmp")
  val TemplateKsi: Mat = Imgcodecs.imread("images/keypad/ksi.bmp")
  val TemplateBlackStar: Mat = Imgcodecs.imread("images/keypad/blackStar.bmp")
  val TemplateThousands: Mat = Imgcodecs.imread("images/keypad/thousands.bmp")
  val TemplateAe: Mat = Imgcodecs.imread("images/keypad/ae.bmp")
  val TemplatePsi: Mat = Imgcodecs.imread("images/keypad/psi.bmp")
  val TemplateShortI: Mat = Imgcodecs.imread("images/keypad/shortI.bmp")
  val TemplateOmega: Mat = Imgcodecs.imread("images/keypad/omega.bmp")
