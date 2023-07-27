package com.zeramorphic.ktane

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

object MatchTemplate:
  def occurrences(image: Mat, template: Mat, threshold: Double, no_confusion: Int): Array[Point] = {
    // Match the image against the template.
    val out = Mat(0, 0, CvType.CV_32F)
    Imgproc.matchTemplate(image, template, out, Imgproc.TM_CCOEFF_NORMED)
    // Remove results that did not match well enough.
    val filtered = Mat()
    Imgproc.threshold(out, filtered, threshold, 0.0, Imgproc.THRESH_TOZERO)

    // Find the local maxima of the filtered results.
    val kernel = Mat(5, 5, CvType.CV_8U, Scalar(1.0))
    kernel.put(2, 2, 0.0)
    val dilated = Mat(out.height, out.width, out.`type`)
    Imgproc.dilate(filtered, dilated, kernel)
    val localMaxima = Mat()
    Core.compare(filtered, dilated, localMaxima, Core.CMP_GT)

    // Find the positions of the local maxima.
    val positions = Mat()
    Core.findNonZero(localMaxima, positions)
    if positions.width * positions.height == 0 then return Array()
    val coordsList: Array[Int] = (0 until positions.width * positions.height * positions.channels).toArray
    positions.get(0, 0, coordsList)
    val pointsList = coordsList.grouped(2)
      .map(coords => Point(coords(0), coords(1)))
      .toArray

    // Get the leftmost matches within the no-confusion range.
    pointsList.iterator
      .zip(pointsList.iterator.drop(1) ++ Array(Point(100000, 0)).iterator)
      .filter((value, next) => (next.x - value.x).abs + (next.y - value.y).abs > no_confusion)
      .map((value, _) => value)
      .toArray
  }

  def occurrences(image: Mat, template: Mat, threshold: Double): Array[Point] =
    occurrences(image, template, threshold, 30)

  def occurrences(image: Mat, template: Mat): Array[Point] =
    occurrences(image, template, 0.5, 30)
