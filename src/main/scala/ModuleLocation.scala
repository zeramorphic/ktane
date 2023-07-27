package com.zeramorphic.ktane

/**
 * @param reverse True if this module is on the reverse side of the bomb.
 * @param row The row on which this module is located. 0 is the bottom.
 * @param column The column in which this module is located. 0 is the left.
 */
case class ModuleLocation(reverse: Boolean,
                          row: Int,
                          column: Int)
