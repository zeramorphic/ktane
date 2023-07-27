package com.zeramorphic.ktane

import org.opencv.core.Point

case class BombDimensions(rows: Int, columns: Int):
  /**
   * The possible locations for a module on this bomb.
   */
  def moduleLocations(): Seq[ModuleLocation] = {
    for (
      side <- Seq(false, true);
      i <- 0 until rows;
      j <- 0 until columns
    ) yield ModuleLocation(side, i, j)
  }

  /**
   * The possible locations for a module on the front of this bomb.
   */
  def obverseModuleLocations(): Seq[ModuleLocation] = {
    for (
      i <- 0 until rows;
      j <- 0 until columns
    ) yield ModuleLocation(false, i, j)
  }

  /**
   * The possible locations for a module on the back of this bomb.
   */
  def reverseModuleLocations(): Seq[ModuleLocation] = {
    for (
      i <- 0 until rows;
      j <- 0 until columns
    ) yield ModuleLocation(true, i, j)
  }

  /**
   * The screen-space position of a module with the given location.
   */
  def position(location: ModuleLocation): Point = position(location.row, location.column)

  /**
   * The screen-space position of a module with the given location.
   */
  def position(row: Int, column: Int): Point = Point(columnPosition(column), rowPosition(row))

  /**
   * The y coordinate of points in modules on this row, when viewed at default zoom level.
   */
  def rowPosition(row: Int): Double = rows match {
    case 2 => 680 - 280 * row
    case _ => throw NotImplementedError(s"not implemented for $rows rows")
  }

  /**
   * The x coordinate of points in modules in this column, when viewed at default zoom level.
   */
  def columnPosition(column: Int): Double = columns match {
    case 3 => 680 + 280 * column
    case _ => throw NotImplementedError(s"not implemented for $rows rows")
  }
