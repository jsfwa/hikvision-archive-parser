package net.jsfwa.hikvision.archive.parser

/**
  * Created by Andrei Zubrilin, 2017
  */

import java.io.{BufferedInputStream, FileInputStream}

import akka.pattern.ask
import akka.util.Timeout
import net.jsfwa.hikvision.archive.parser.helpers.HexConverter._
import net.jsfwa.hikvision.archive.parser.helpers.AsyncHelper._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait InfoParser{
  def parse() : HInfo
}

/**
  * Hikvision info file structure
  *
  * @param serialNumber Serial number
  * @param macAddress   Mac address
  * @param res          No info
  * @param size         No info
  * @param blocks       No info
  * @param dataDirs     Directories
  * @param indices      List of index files
  */
case class HInfo(serialNumber: String,
                 macAddress: String,
                 res: Array[Byte],
                 size: Int,
                 blocks: BigInt,
                 dataDirs: Int,
                 indices: Seq[HIndex]) {

  override def toString: String =
    s"$serialNumber, $macAddress, $res, $size, $blocks, $dataDirs\r\n${indices.map(_.toString + "\r\n")}"
}

/**
  * Hikvision Info Structure
  *
  * | serialNumber | macAddress | res | size | blocks | dataDirs |
  * |--------------|------------|-----|------|--------|----------|
  * |      48b     |     6b     |  2b |  4b  |   4b   |    4b    |
  *
  */
class DefaultInfoParser(dir: String, archiveSettings: ArchiveSettings) extends InfoParser {
  import archiveSettings._

  def makePath(path: String, idx: Int) = {
    s"$path/datadir$idx"
  }

  override def parse(): HInfo = {
    val path = s"$dir/$INFO_FILE_NAME"
    val bis = new BufferedInputStream(new FileInputStream(path))
    val arr = Array.fill[Byte](INFO_LEN)(0)

    bis.read(arr)

    fromArray(arr)
  }


  def fromArray(arr: Array[Byte]): HInfo = {
    val dataDirs = Integer.parseInt(arr.toHexString(64, 64 + 4), 16) // 4b length, 64b offset - amount of directories

    HInfo(
      serialNumber = arr.take(48).map(_.toChar).mkString, // 48b length - serial number
      macAddress = arr.slice(48, 48 + 6).map(_.toHexString).mkString(":"), // 6b length, 48 offset
      res = arr.slice(54, 54 + 2), //never used
      size = Integer.parseInt(arr.toHexString(56, 56 + 4), 16), //never used
      blocks = BigInt(arr.toHexString(60, 60 + 4), 16), //never used
      dataDirs = dataDirs,
      indices = awaitSeq(0.until(dataDirs).map {
        idx =>
          new DefaultIndexParser(makePath(dir, idx), archiveSettings).parseAsync()
      })
    )
  }
}