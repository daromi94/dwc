package com.daromi.dwc.core

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{
  AbstractBehavior,
  ActorContext,
  Behaviors
}

sealed trait GuardianMessage

final class Guardian(
    context: ActorContext[GuardianMessage]
) extends AbstractBehavior[GuardianMessage](context):

  override def onMessage(msg: GuardianMessage): Behavior[GuardianMessage] =
    msg match
      case Guardian.Start =>
        val counter          = context.spawnAnonymous(Counter())
        val workerSupervisor = context.spawnAnonymous(WorkerSupervisor())

        workerSupervisor ! WorkerSupervisor.Start(counter)

        this

object Guardian:

  case object Start extends GuardianMessage

  def apply(): Behavior[GuardianMessage] =
    Behaviors.setup(context => new Guardian(context))
