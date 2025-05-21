package com.daromi.dwc.core

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

sealed trait ApplicationMessage

final class Application(
    context: ActorContext[ApplicationMessage]
) extends AbstractBehavior[ApplicationMessage](context):

  override def onMessage(
      msg: ApplicationMessage
  ): Behavior[ApplicationMessage] =
    msg match
      case Application.Start =>
        val counter          = context.spawn(Counter(), "counter")
        val workerSupervisor = context.spawn(WorkerSupervisor(), "worker-supervisor")

        workerSupervisor ! WorkerSupervisor.Start(counter)

        this

object Application:

  case object Start extends ApplicationMessage

  def apply(): Behavior[ApplicationMessage] = Behaviors.setup(context => new Application(context))
