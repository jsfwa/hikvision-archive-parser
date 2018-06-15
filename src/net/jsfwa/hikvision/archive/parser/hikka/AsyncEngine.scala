package net.jsfwa.hikvision.archive.parser.hikka

import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import net.jsfwa.hikvision.archive.parser.hikka.ActorWorker.AsyncOp

import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Andrei Zubrilin, 2018
  */
trait AsyncEngine {

  def run[A](a: AsyncOp[A]) : Future[A]
}


class DefaultAsyncEngine extends AsyncEngine {

  val sys = new WorkerSystemSetup with WorkerConfig
  import scala.concurrent.duration._
  implicit val timeout: Timeout = Timeout(60 seconds)

  override def run[A](a: AsyncOp[A]): Future[A] = {
    (sys.indexWorker ? a).map(_.asInstanceOf[A])
  }
}