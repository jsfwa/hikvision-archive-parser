package net.jsfwa.hikvision.archive.parser.helpers

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Andrei Zubrilin, 2018
  */
object AsyncHelper {

  def await[A](future: Future[A], duration: Duration = Duration.Inf) : A = Await.result(future, duration)

  def awaitSeq[A](futures: Seq[Future[A]], duration: Duration = Duration.Inf) : Seq[A]= await(Future.sequence(futures), duration)
}
