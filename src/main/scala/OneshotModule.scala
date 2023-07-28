package com.zeramorphic.ktane
import java.time.Instant

/**
 * A module that we will attempt to solve (as in `attemptSolve`) exactly once.
 * After that, it will be marked as solved.
 */
abstract class OneshotModule extends Module:
  private var isSolved = false

  override def solved: Boolean = isSolved

  override def attemptSolve(): Option[Instant] = {
    solve()
    isSolved = true
    None
  }
  
  def solve(): Unit
