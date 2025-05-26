package com.daromi.dwc.core.actor

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{ActorContext, Behaviors}

object Counter:

  sealed trait Command
  case object Increment extends Command

  private final case class State(count: Int)

  def apply(): Behavior[Command] =
    val initial = State(0)

    active(initial)

  private def active(state: State): Behavior[Command] =
    Behaviors.receive { (ctx, message) =>
      message match
        case Increment => handleIncrement(ctx)(state)
    }

  private def handleIncrement(ctx: ActorContext[Command])(state: State): Behavior[Command] =
    val next = State(state.count + 1)

    ctx.log.info(s"${ctx.self}: count=${next.count}")

    active(next)
