package net.jsfwa.hikvision.archive.parser

/**
  * Created by Andrei Zubrilin, 2017
  */

import net.jsfwa.hikvision.archive.parser.helpers.HexConverter._
import org.joda.time.DateTime

trait FileParser{
  def parse(arr: Array[Byte]) : HFile
}

/**
  * Hikvision file structure
  *
  * @param path         File path
  * @param fileNo       File number
  * @param channel      Camera channel, default is 0
  * @param segRecNums   Amount of segments in file
  * @param startTime    First segment start time
  * @param endTime      Last segment end time
  * @param status       No info found
  * @param res1         No info found
  * @param lockedSegNum No info found
  * @param res2         No info found
  * @param infoTypes    No info found
  */
case class HFile(path: String,
                 fileNo: BigInt,
                 channel: BigInt,
                 segRecNums: BigInt,
                 startTime: DateTime,
                 endTime: DateTime,
                 status: Int,
                 res1: Int,
                 lockedSegNum: BigInt,
                 res2: Array[Byte],
                 infoTypes: Array[Byte]
                ) {
}

/**
  * Hikvision File Structure
  *
  * | fileNo | channel | segRecNums | startTime | endTime | status | res1 | lockedSegNum | res2 | infoTypes |
  * |--------|---------|------------|-----------|---------|--------|------|--------------|------|-----------|
  * |   4b   |    2b   |     2b     |     4b    |    4b   |   1b   |  1b  |      2b      |  4b  |     8b    |
  *
  * @param path
  */
class DefaultFileParser(path: String) extends FileParser {

  /**
    * Make file from byte array
    *
    * @param arr 32 bytes array with file information
    * @return
    */
  override def parse(arr: Array[Byte]): HFile = {
    HFile(
      path = this.path + "hiv" + f"${BigInt(arr.toHexString(0, 4), 16).toInt}%05d.mp4", // First 4 bytes - number of physical file
      fileNo = BigInt(arr.toHexString(0, 4), 16),
      channel = BigInt(arr.toHexString(4, 4 + 2), 16), // 2 bytes with 4 bytes offset - channel number
      segRecNums = BigInt(arr.toHexString(6, 6 + 2), 16), // 2 bytes with 6 bytes offset - segments amount
      startTime = new DateTime(BigInt(arr.toHexString(8, 8 + 4), 16).toLong * 1000).minusHours(3), // 4b, 8b offset - start time
      endTime = new DateTime(BigInt(arr.toHexString(12, 12 + 4), 16).toLong * 1000).minusHours(3), // 4b, 12 bytes offset - end time
      status = arr(16), //never used
      res1 = arr(17), //never used
      lockedSegNum = BigInt(arr.toHexString(18, 18 + 2), 16), //never used
      res2 = arr.slice(20, 20 + 4), //never used
      infoTypes = arr.takeRight(8) //never used
    )
  }
}
