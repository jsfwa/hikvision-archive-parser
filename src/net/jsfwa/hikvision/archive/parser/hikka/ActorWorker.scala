package net.jsfwa.hikvision.archive.parser.hikka

import akka.actor.{Actor, Props}
import net.jsfwa.hikvision.archive.parser.{ArchiveSettings, DefaultIndexParser, HIndex, IndexParser}
import net.jsfwa.hikvision.archive.parser.hikka.ActorWorker.{AsyncOp}

/**
  * Created by Andrei Zubrilin, 2018
  */
object ActorWorker {

  def props = Props[ActorWorker]

  sealed trait Operation

  case class AsyncOp[A](op: () => A) extends Operation

}

class ActorWorker extends Actor {
  override def receive: Receive = {
    case p: AsyncOp[_] =>
      sender ! p.op()
  }
}
