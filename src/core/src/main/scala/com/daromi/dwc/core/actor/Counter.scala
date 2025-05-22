package com.daromi.dwc.core.actor

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{ActorContext, Behaviors}

object Counter:

  sealed trait Command
  case object Increment extends Command
  case object Stop      extends Command

  private final case class State(count: Int)

  def apply(): Behavior[Command] = react(State(0))

  private def react(state: State): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match
        case Increment => handleIncrement(context, state)
        case Stop      => Behaviors.stopped
    }

  private def handleIncrement(
      context: ActorContext[Command],
      state: State
  ): Behavior[Command] =
    val next = State(state.count + 1)

    println(s"${context.self}: count=${next.count}")

    react(next)
