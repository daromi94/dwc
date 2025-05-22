package com.daromi.dwc.core

import com.daromi.dwc.core.actor.Application
import org.apache.pekko.actor.typed.ActorSystem

@main def main(): Unit =
  val system = ActorSystem.create(Application(), "application")
  system ! Application.Start
