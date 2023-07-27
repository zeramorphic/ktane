package com.zeramorphic.ktane

object CategoriseModule:
  def categorise(interactions: Interactions, location: ModuleLocation, dimensions: BombDimensions): String = {
    interactions.selectModule(location, dimensions)
    val screenshot = interactions.screenshotSelectedModule()
    interactions.deselect()

    val modules = Seq(
      ("button", Seq((CategoriseTemplates.Button, 0.9))),
      ("complicated_wires", Seq((CategoriseTemplates.ComplicatedWires, 0.9))),
      ("maze", Seq((CategoriseTemplates.Maze, 0.9))),
      ("memory", Seq(
        (CategoriseTemplates.MemoryLeft, 0.9),
        (CategoriseTemplates.MemoryRight, 0.9)
      )),
      ("morse", Seq((CategoriseTemplates.Morse, 0.9))),
      ("password", Seq((CategoriseTemplates.Password, 0.9))),
      ("simon_says", Seq((CategoriseTemplates.SimonSays1, 0.9))),
      ("simon_says", Seq((CategoriseTemplates.SimonSays2, 0.9))),
      ("simon_says", Seq((CategoriseTemplates.SimonSays3, 0.9))),
      ("simon_says", Seq((CategoriseTemplates.SimonSays4, 0.9))),
      ("symbols", Seq((CategoriseTemplates.Symbols, 0.9))),
      ("whos_on_first", Seq(
        (CategoriseTemplates.WhosOnFirstDisplay, 0.9),
        (CategoriseTemplates.WhosOnFirstProgress, 0.9)
      )),
      ("wire_sequences", Seq(
        (CategoriseTemplates.WireSequencesArrow, 0.9),
        (CategoriseTemplates.WireSequencesProgress, 0.9)
      )),
      ("wires", Seq(
        (CategoriseTemplates.WiresLeft, 0.9),
        (CategoriseTemplates.WiresRight, 0.9)
      )),
    )

    val options = modules.filter((name, templates) =>
        templates.forall((template, threshold) =>
          MatchTemplate.occurrences(screenshot, template, threshold).nonEmpty))
      .map((name, templates) => name)
      .distinct
      .toList

    options.length match {
      case 0 =>
        println("could not determine module")
        "unknown"
      case 1 =>
        println("detected " + options.head)
        options.head
      case _ =>
        println("ambiguous: " + options)
        "ambiguous"
    }
  }
