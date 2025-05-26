package com.daromi.dwc.core.actor

import org.apache.pekko.actor.typed.scaladsl.{ActorContext, Behaviors}
import org.apache.pekko.actor.typed.{ActorRef, Behavior}

object Worker:

  sealed trait Command
  final case class Start(counter: ActorRef[Counter.Command]) extends Command
  case object Stop                                           extends Command

  def apply(): Behavior[Command] = react()

  private def react(): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case Start(counter) => handleStart(context, counter)
        case Stop           => Behaviors.stopped
      }
    }

  private def handleStart(
      context: ActorContext[Command],
      counter: ActorRef[Counter.Command]
  ): Behavior[Command] =
    for _ <- 1 to 1_000 do counter ! Counter.Increment

    Behaviors.same
