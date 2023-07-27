package com.zeramorphic.ktane

import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import javax.imageio.ImageIO

object ImageConversion:
  def bufferedImageToMat(image: BufferedImage): Mat = {
    val stream = ByteArrayOutputStream()
    ImageIO.write(image, "bmp", stream)
    stream.flush()
    Imgcodecs.imdecode(MatOfByte(stream.toByteArray: _*), Imgcodecs.IMREAD_UNCHANGED)
  }

  def matToBufferedImage(matrix: Mat): BufferedImage = {
    val matrixOfByte = MatOfByte()
    Imgcodecs.imencode(".bmp", matrix, matrixOfByte)
    ImageIO.read(ByteArrayInputStream(matrixOfByte.toArray))
  }

  def writeToFile(matrix: Mat, name: String): Boolean = {
    if matrix.`type`() == CvType.CV_32F then {
      val outScaled = Mat()
      Core.multiply(matrix, Scalar(255.0), outScaled)
      val outU8 = Mat(0, 0, CvType.CV_8UC1)
      outScaled.convertTo(outU8, CvType.CV_8UC1)
      ImageIO.write(ImageConversion.matToBufferedImage(outU8), "bmp", new File(name + ".bmp"))
    } else {
      ImageIO.write(ImageConversion.matToBufferedImage(matrix), "bmp", new File(name + ".bmp"))
    }
  }
