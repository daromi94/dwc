package com.daromi.dwc.core

import org.apache.pekko.actor.typed.ActorSystem

@main def main(): Unit =
  val system = ActorSystem(Application(), "application")
  system ! Application.Start
