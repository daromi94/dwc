package com.daromi.dwc.core

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

sealed trait CounterMessage

final class Counter(
    context: ActorContext[CounterMessage]
) extends AbstractBehavior[CounterMessage](context):

  private var count: Int = 0

  override def onMessage(msg: CounterMessage): Behavior[CounterMessage] =
    msg match
      case Counter.Increment =>
        count += 1
        println(s"${context.self}: count=$count")
        this

object Counter:

  case object Increment extends CounterMessage

  def apply(): Behavior[CounterMessage] = Behaviors.setup(context => new Counter(context))
