package com.zeramorphic.ktane

import modules.*

import java.time.Instant

/**
 * Base trait for all solvable modules on the bomb.
 * When adding a new module,
 * <ul>
 *   <li>update `Module::fromName`</li>
 *   <li>update `CategoriseModule::categorise`</li>
 * </ul>
 */
trait Module:
  /**
   * True if this module is solved.
   */
  def solved: Boolean

  /**
   * Assuming that this module is focused, attempt to solve it.
   * Must not deselect the module after attempting a solve.
   *
   * @return A timestamp at which we should check the module again to see if it can be solved.
   *         This is useful for modules with animations so that we can do other work while animations are playing,
   *         and for modules that can only be solved at specific times like The Swan or Ice Cream.
   *         If this is null, we will not revisit the module.
   */
  def attemptSolve(): Option[Instant]

  /**
   * How early should we start trying to solve this module?
   * Higher is earlier. 0 is the default.
   */
  def priority: Int = 0

object Module:
  /**
   * Tries to find a module with the given name.
   * If one did not exist, return `null`.
   */
  def fromName(name: String, interactions: Interactions, edgework: Edgework): Module = {
    name match {
      case "complicated_wires" => ComplicatedWires(interactions, edgework)
      case "keypad" => Keypad(interactions)
      case "maze" => Maze(interactions)
      case "memory" => Memory(interactions)
      case "password" => Password(interactions)
      case "wires" => Wires(interactions, edgework)
      case _ => null
    }
  }
