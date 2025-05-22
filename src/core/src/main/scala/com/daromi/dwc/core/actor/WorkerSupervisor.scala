package com.daromi.dwc.core.actor

import org.apache.pekko.actor.typed.scaladsl.{ActorContext, Behaviors}
import org.apache.pekko.actor.typed.{ActorRef, Behavior, SupervisorStrategy}

object WorkerSupervisor:

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
    val workers = spawnWorkers(context)

    workers.foreach { _ ! Worker.Start(counter) }
    workers.foreach { _ ! Worker.Stop }

    Behaviors.same

  private def spawnWorkers(context: ActorContext[Command]): Seq[ActorRef[Worker.Command]] =
    (1 to 1000).map { i =>
      val name     = s"worker-$i"
      val behavior = Behaviors.supervise(Worker()).onFailure(SupervisorStrategy.restart)

      context.spawn(behavior, name)
    }
