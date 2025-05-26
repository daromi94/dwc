package com.daromi.dwc.core.actor

import org.apache.pekko.actor.typed.scaladsl.{ActorContext, Behaviors}
import org.apache.pekko.actor.typed.{ActorRef, Behavior, SupervisorStrategy}

object Application:

  sealed trait Command
  case object Start extends Command

  def apply(): Behavior[Command] =
    val behavior = active()

    Behaviors.supervise(behavior).onFailure(SupervisorStrategy.restart)

  private def active(): Behavior[Command] =
    Behaviors.setup { ctx =>
      val counter = spawnCounter(ctx)
      val workers = spawnWorkers(ctx)

      Behaviors.receiveMessage { case Start => handleStart(counter, workers) }
    }

  private def spawnCounter(ctx: ActorContext[Command]): ActorRef[Counter.Command] =
    val behavior = Counter()

    val supervised = Behaviors.supervise(behavior).onFailure(SupervisorStrategy.restart)

    ctx.spawn(supervised, "Counter")

  private def spawnWorkers(ctx: ActorContext[Command]): Seq[ActorRef[Worker.Command]] =
    (1 to 1_000).map { i =>
      val behavior = Worker()

      val supervised = Behaviors.supervise(behavior).onFailure(SupervisorStrategy.restart)

      ctx.spawn(supervised, s"Worker$i")
    }

  private def handleStart(
      counter: ActorRef[Counter.Command],
      workers: Seq[ActorRef[Worker.Command]]
  ): Behavior[Command] =
    workers.foreach { _ ! Worker.Run(counter) }

    Behaviors.same
