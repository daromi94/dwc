package com.daromi.dwc.core.actor

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{ActorContext, Behaviors}

object Application:

  sealed trait Command
  case object Start extends Command
  case object Stop  extends Command

  def apply(): Behavior[Command] = react()

  private def react(): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match
        case Start => handleStart(context)
        case Stop  => Behaviors.stopped
    }

  private def handleStart(context: ActorContext[Command]): Behavior[Command] =
    val counter          = context.spawn(Counter(), "counter")
    val workerSupervisor = context.spawn(WorkerSupervisor(), "worker-supervisor")

    workerSupervisor ! WorkerSupervisor.Start(counter)

    Behaviors.same
