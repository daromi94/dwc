package com.daromi.dwc.core

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

final class Counter(
    context: ActorContext[Counter.Message]
) extends AbstractBehavior[Counter.Message](context):

  private var count: Int = 0

  override def onMessage(msg: Counter.Message): Behavior[Counter.Message] =
    msg match
      case Counter.Increment =>
        count += 1
        println(s"${context.self}: count=$count")
        this

object Counter:

  def apply(): Behavior[Message] = Behaviors.setup(context => new Counter(context))

  sealed trait Message
  case object Increment extends Message
