package com.daromi.dwc.core

import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import org.apache.pekko.actor.typed.{ActorRef, Behavior, SupervisorStrategy}

final class WorkerSupervisor(
    context: ActorContext[WorkerSupervisor.Message]
) extends AbstractBehavior[WorkerSupervisor.Message](context):

  override def onMessage(msg: WorkerSupervisor.Message): Behavior[WorkerSupervisor.Message] =
    msg match
      case WorkerSupervisor.Start(counter) =>
        val workers = spawnWorkers()

        workers.foreach { _ ! Worker.Start(counter) }
        workers.foreach { _ ! Worker.Stop }

        this

  private def spawnWorkers(): Seq[ActorRef[Worker.Message]] =
    (1 to 1000).map { i =>
      val behavior = Behaviors.supervise(Worker()).onFailure(SupervisorStrategy.restart)
      context.spawn(behavior, s"worker-$i")
    }

object WorkerSupervisor:

  def apply(): Behavior[Message] = Behaviors.setup(context => new WorkerSupervisor(context))

  sealed trait Message
  final case class Start(counter: ActorRef[Counter.Message]) extends Message

final class Worker(
    context: ActorContext[Worker.Message]
) extends AbstractBehavior[Worker.Message](context):

  override def onMessage(msg: Worker.Message): Behavior[Worker.Message] =
    msg match
      case Worker.Start(counter) =>
        for i <- 1 to 1000 do counter ! Counter.Increment
        this

      case Worker.Stop => Behaviors.stopped

object Worker:

  def apply(): Behavior[Message] = Behaviors.setup(context => new Worker(context))

  sealed trait Message
  final case class Start(counter: ActorRef[Counter.Message]) extends Message
  case object Stop                                           extends Message
