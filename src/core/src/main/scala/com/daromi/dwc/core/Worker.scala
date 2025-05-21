package com.daromi.dwc.core

import org.apache.pekko.actor.typed.scaladsl.{
  AbstractBehavior,
  ActorContext,
  Behaviors
}
import org.apache.pekko.actor.typed.{ActorRef, Behavior, SupervisorStrategy}

sealed trait WorkerSupervisorMessage

final class WorkerSupervisor(
    context: ActorContext[WorkerSupervisorMessage]
) extends AbstractBehavior[WorkerSupervisorMessage](context):

  override def onMessage(
      msg: WorkerSupervisorMessage
  ): Behavior[WorkerSupervisorMessage] =
    msg match
      case WorkerSupervisor.Start(counter) =>
        val workers = (1 to 1000) map { i =>
          context.spawn(
            Behaviors.supervise(Worker()).onFailure(SupervisorStrategy.restart),
            s"worker-$i"
          )
        }
        workers.foreach { _ ! Worker.Start(counter) }
        workers.foreach { _ ! Worker.Stop }
        this

object WorkerSupervisor:

  case class Start(
      counter: ActorRef[CounterMessage]
  ) extends WorkerSupervisorMessage

  def apply(): Behavior[WorkerSupervisorMessage] =
    Behaviors.setup(context => new WorkerSupervisor(context))

sealed trait WorkerMessage

final class Worker(
    context: ActorContext[WorkerMessage]
) extends AbstractBehavior[WorkerMessage](context):

  override def onMessage(msg: WorkerMessage): Behavior[WorkerMessage] =
    msg match
      case Worker.Start(counter) =>
        for i <- 1 to 1000 do counter ! Counter.Increment
        this

      case Worker.Stop => Behaviors.stopped

object Worker:

  case class Start(counter: ActorRef[CounterMessage]) extends WorkerMessage
  case object Stop                                    extends WorkerMessage

  def apply(): Behavior[WorkerMessage] =
    Behaviors.setup(context => new Worker(context))
