package com.daromi.dwc.core

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

final class Application(
    context: ActorContext[Application.Message]
) extends AbstractBehavior[Application.Message](context):

  override def onMessage(msg: Application.Message): Behavior[Application.Message] =
    msg match
      case Application.Start =>
        val counter          = context.spawn(Counter(), "counter")
        val workerSupervisor = context.spawn(WorkerSupervisor(), "worker-supervisor")

        workerSupervisor ! WorkerSupervisor.Start(counter)

        this

object Application:

  def apply(): Behavior[Message] = Behaviors.setup(context => new Application(context))

  sealed trait Message
  case object Start extends Message
