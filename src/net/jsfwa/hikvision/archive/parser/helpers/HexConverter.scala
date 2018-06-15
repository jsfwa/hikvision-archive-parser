package net.jsfwa.hikvision.archive.parser.helpers

import scala.math.ScalaNumber
import scala.runtime.RichInt
import scala.util.Try

/**
  * Created by Andrei Zubrilin, 2018
  */
object HexConverter {

  /**
    * Convert number to hex formatted "x" => "0x"
    *
    * @param v
    */
  implicit class HexIntFormat[A <: AnyVal](v: A) {
    def toHex: String = {

      val hexNum = v match {
        case x: Byte => x.toInt.toHexString
        case _ => BigInt(v.toString).toString(16)
      }
      s"0$hexNum" takeRight 2
    }
  }


  /**
    * Convert number array to big-endian hex string
    *
    * @param arr
    */
  implicit class HexIntArrayFormat[A <: AnyVal](arr: Array[A]){
    def toHexString(start: Int = 0, end: Int = -1): String = arr.slice(start, end).map(_.toHex).reverse.mkString
  }

}