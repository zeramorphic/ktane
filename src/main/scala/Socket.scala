package com.zeramorphic.ktane

import java.time.Instant

/**
 * A socket on the bomb, in a location, containing a module.
 *
 * @param name The computer readable name of the module in this socket, or `null` if no module is present.
 */
class Socket(val location: ModuleLocation, val name: String, interactions: Interactions, edgework: Edgework):
  /**
   * The module inserted in this socket.
   */
  val module: Module = Module.fromName(name, interactions, edgework)

  /**
   * The time at which we can next try to solve this module.
   */
  var checkNext: Instant = Instant.now()
