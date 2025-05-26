package com.daromi.dwc.core.actor

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior}

object Worker:

  sealed trait Command
  final case class Run(counter: ActorRef[Counter.Command]) extends Command

  def apply(): Behavior[Command] = active()

  private def active(): Behavior[Command] =
    Behaviors.receiveMessage { case Run(counter) => handleRun(counter) }

  private def handleRun(counter: ActorRef[Counter.Command]): Behavior[Command] =
    for _ <- 1 to 1_000 do counter ! Counter.Increment

    Behaviors.stopped
