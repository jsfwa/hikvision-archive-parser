package net.jsfwa.hikvision.archive.parser.hikka

import akka.actor.{ActorRef, ActorSystem}
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
  * Created by Andrei Zubrilin, 2018
  */

trait WorkerConfig {
  val config = ConfigFactory.load()

  lazy val systemName: String = Try(config.getString("hikvision.akka.system")).toOption.getOrElse("hikvision-akka")

  lazy val indexWorkersNum: Int = Try(config.getInt("hikvision.akka.workers.index")).toOption.getOrElse(10)
}

class WorkerSystemSetup {
  self: WorkerConfig =>

  lazy val akkaSystem = ActorSystem(self.systemName)

  lazy val indexWorker: ActorRef = akkaSystem.actorOf(RoundRobinPool(self.indexWorkersNum).props(ActorWorker.props), "hikvision-index-worker")
}
