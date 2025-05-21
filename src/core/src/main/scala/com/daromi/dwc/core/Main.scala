package com.daromi.dwc.core

import org.apache.pekko.actor.typed.ActorSystem

@main def main(): Unit =
  val system = ActorSystem.create(Guardian(), "guardian")
  system ! Guardian.Start
