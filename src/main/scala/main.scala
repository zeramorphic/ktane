package com.zeramorphic.ktane

import org.opencv.core.{Core, CvType, Mat, MatOfByte, Point, Scalar}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

import java.awt.image.{BufferedImage, DataBufferByte, DataBufferInt}
import java.awt.{GraphicsEnvironment, Rectangle, Robot}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import javax.imageio.ImageIO

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

@main
def main(): Unit = {
  val opencvVersion = Core.VERSION
  println(s"loading OpenCV: $opencvVersion")
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
  System.loadLibrary("opencv_java480")
  println("loading done")
  val first_mat = Mat()

  val screen = GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices()(0)
  println(s"using screen $screen")

  val robot = Robot()
  //  robot.mouseMove(screen.getDefaultConfiguration.getBounds.x + 5, screen.getDefaultConfiguration.getBounds.y + 5)

  val capture = robot.createScreenCapture(Rectangle(
    screen.getDefaultConfiguration.getBounds.x, screen.getDefaultConfiguration.getBounds.y,
    1920, 1080))
  val mat = bufferedImageToMat(capture)
  Imgproc.rectangle(mat, Point(5, 5), Point(10, 10), Scalar(255.0, 0.0, 255.0))
  ImageIO.write(matToBufferedImage(mat), "bmp", new File("test.bmp"))
}
