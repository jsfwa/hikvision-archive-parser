package net.jsfwa.hikvision.archive.parser

/**
  * Created by Andrei Zubrilin, 2017
  */

import java.io.{BufferedInputStream, FileInputStream}

import net.jsfwa.hikvision.archive.parser.helpers.HexConverter._
import net.jsfwa.hikvision.archive.parser.hikka.ActorWorker.AsyncOp
import net.jsfwa.hikvision.archive.parser.hikka.{AsyncEngine, DefaultAsyncEngine}

import scala.concurrent.Future

trait IndexParser {
  def parse(): HIndex

  def parseAsync(): Future[HIndex]
}

/**
  * Hikvision index file structure
  *
  * @param modifyTimes   Last modify time
  * @param version       Current version
  * @param fileCount     Amount of files
  * @param nextFileRecNo Next file number for recording
  * @param lastFileRecNo Last file number
  * @param currFileRecNo Current file
  * @param res           No info found
  * @param checksum
  * @param files         List of files
  * @param segments      List of segments
  */
case class HIndex(modifyTimes: BigInt,
                  version: BigInt,
                  fileCount: BigInt,
                  nextFileRecNo: BigInt,
                  lastFileRecNo: BigInt,
                  currFileRecNo: Array[Byte],
                  res: Array[Byte],
                  checksum: BigInt,
                  files: List[HFile],
                  segments: List[HSegment]
                 ) {
  override def toString: String =
    s"$modifyTimes, $version, $fileCount, $nextFileRecNo, $lastFileRecNo, $currFileRecNo, $res, $checksum\r\n${files.map(_.toString + "\r\n")}\r\n${segments.map(_.toString + "\r\n")}"
}

/**
  * Hikvision Index File Structure
  *
  * | modifyTimes | version | filecount | nextFileRecNo | lastFileRecNo | currFileRecNo | res | checksum |
  * |-------------|---------|-----------|---------------|---------------|---------------|-----|----------|
  * |      8b     |    4b   |    4b     |       4b      |       4b      |     1176b     | 76b |    4b    |
  */
class DefaultIndexParser(path: String, archiveSettings: ArchiveSettings) extends IndexParser {

  import archiveSettings._


  def parseAsync(asyncEngine: AsyncEngine): Future[HIndex] = {
    asyncEngine.run(AsyncOp(parse))
  }

  override def parseAsync(): Future[HIndex] = {
    parseAsync(new DefaultAsyncEngine)
  }

  override def parse(): HIndex = {

    //File to byte array
    val bis = new BufferedInputStream(new FileInputStream(s"$path/$INDEX_FILE_NAME"))
    val bArray = Array.fill[Byte](INDEX_LEN)(0)
    bis.read(bArray)

    //Parsing byte array
    val fileCount = BigInt(bArray.toHexString(12, 12 + 4), 16).toInt // 4 bytes length, 12 bytes offset - amount of files
    val lastFileRecNo = BigInt(bArray.toHexString(20, 20 + 4), 16) // 4b length, 20b offset - last file number
    var curFile = bArray.slice(48, 48 + 1152) // 1152b length, 48b offset - current file without header (why keep it in index file memory?! Ask chinese)
    curFile = curFile.take(2) ++ Array[Byte](0, 0, 0, 0) ++ curFile.drop(2) // Current file structure is corrupted need to transform it to default file for successful parsing in future

    HIndex(
      modifyTimes = BigInt(bArray.toHexString(0, 8), 16), // 8b length - modify time
      version = BigInt(bArray.toHexString(8, 8 + 4), 16), // 4b length, 8b offset - version
      fileCount = fileCount,
      nextFileRecNo = BigInt(bArray.toHexString(16, 16 + 4), 16), // 4b length, 16b offset - next file
      lastFileRecNo = lastFileRecNo,
      currFileRecNo = bArray.slice(24, 24 + 1176), // 1176b length, 48b offset - current file (full with header info)
      res = bArray.slice(1200, 1200 + 76), //never used
      checksum = BigInt(bArray.toHexString(1276, 1276 + 4), 16), // 4b length, 1276b offset - checksum
      files = {
        getFiles(bis, fileCount, path, curFile)
      },
      segments = {
        getSegments(bis, fileCount)
      }
    )
  }

  //Parsing file section
  def getFiles(bis: BufferedInputStream, fileCount: Int, path: String, curFile: Array[Byte]): List[HFile] = {
    val hikvisionFile = new DefaultFileParser(path)
    val inputArray = Array.fill[Byte](FILE_LEN)(0)
    0.until(fileCount).flatMap { i =>
      bis.read(inputArray)
      inputArray match {
        case arr if arr(4) != -1 => Some(hikvisionFile.parse(arr))
        case _ => None
      }
    }.toList ++
      (if (curFile(0) != -1) Some(hikvisionFile.parse(curFile)) else None) // Just check if have current file in memory
  }

  //Parsing index section
  def getSegments(bis: BufferedInputStream, fileCount: Int): List[HSegment] = {
    val hikvisionSegment = new DefaultSegmentParser()
    val inputArray = Array.fill[Byte](SEGMENT_LEN)(0)

    0.until(fileCount * 256).flatMap { i =>
      bis.read(inputArray)
      inputArray match {
        case arr if arr(16) != 0 => Some(hikvisionSegment.parse(arr))
        case _ => None
      }
    }.toList
  }
}
