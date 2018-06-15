package net.jsfwa.hikvision.archive.parser

/**
  * Created by Andrei Zubrilin, 2017
  */

trait SegmentParser{
  def parse(arr: Array[Byte]) : HSegment
}

import net.jsfwa.hikvision.archive.parser.helpers.HexConverter._
import org.joda.time.DateTime

/**
  * Hikvision segment structure
  *
  * @param bType                No info found
  * @param status               No info found
  * @param res1                 No info found
  * @param resolution           Resolution
  * @param startTime            Start time
  * @param endTime              End time
  * @param firstKeyFrameAbsTime First frame absolute time - usually this one is only correct
  * @param firstKeyFrameStdTime First frame ??? time - no info found
  * @param lastKeyFrameStdTime  Last frame ??? time - no info found
  * @param startOffset          Start offset in bytes
  * @param endOffset            End offset in bytes
  * @param res2                 No info found
  * @param infoNum              No info found
  * @param infoTypes            No info found
  * @param infoStartTime        No info found
  * @param infoEndTime          No info found
  * @param infoStartOffset      No info found
  * @param infoEndOffset        No info found
  */
case class HSegment(
                     bType: Int,
                     status: Int,
                     res1: Array[Byte],
                     resolution: Array[Byte],
                     startTime: DateTime,
                     endTime: DateTime,
                     firstKeyFrameAbsTime: DateTime,
                     firstKeyFrameStdTime: DateTime,
                     lastKeyFrameStdTime: DateTime,
                     startOffset: BigInt,
                     endOffset: BigInt,
                     res2: Array[Byte],
                     infoNum: Array[Byte],
                     infoTypes: Array[Byte],
                     infoStartTime: Array[Byte],
                     infoEndTime: Array[Byte],
                     infoStartOffset: Array[Byte],
                     infoEndOffset: Array[Byte])


/**
  * Hikvision Segment Structure
  *
  * | bType | status | res1 | resolution | startTime | endTime | firstKeyFrameAbsTime | firstKeyFrameStdTime | lastKeyFrameStdTime | startOffset | endOffset | res2 | infoNum | infoTypes | infoStartTime | infoEndTime| infoStartOffeset | infoEndOffset |
  * |-------|--------|------|------------|-----------|---------|----------------------|----------------------|---------------------|-------------|-----------|------|---------|-----------|---------------|------------|------------------|---------------|
  * |   1b  |   1b   |  2b  |     4b     |     8b    |    8b   |          8b          |           8b         |         8b          |      4b     |     4b    |  4b  |    4b   |    8b     |      4b       |     4b     |        4b        |       4b      |
  *
  */
class DefaultSegmentParser {

  /**
    * Make segment from byte array
    *
    * @param arr
    * @return
    */
  def parse(arr: Array[Byte]): HSegment = {
    HSegment(
      bType = arr(0), // 1b length - never user
      status = arr(1), // 1b length, 1b offset - never used
      res1 = arr.slice(2, 2 + 2), // 2b length, 2b offset - never used
      resolution = arr.slice(4, 4 + 4), // 4b length, 4b offset - resolution
      startTime = new DateTime(BigInt(arr.toHexString(8, 8 + 4), 16).toLong * 1000).minusHours(3), // 4b length + 4b skipped, 8b offset - start time
      endTime = new DateTime(BigInt(arr.toHexString(16, 16 + 4), 16).toLong * 1000).minusHours(3), // 4b length + 4b skipped, 16b offset - end time
      firstKeyFrameAbsTime = new DateTime(BigInt(arr.toHexString(24, 24 + 4), 16).toLong * 1000).minusHours(3), // 4b length + 4b skipped, 24b offset - never used
      firstKeyFrameStdTime = new DateTime(BigInt(arr.toHexString(32, 32 + 4), 16).toLong * 1000).minusHours(3), // 4b length + 4b skipped, 32b offset - never used
      lastKeyFrameStdTime = new DateTime(BigInt(arr.toHexString(36, 36 + 4), 16).toLong * 1000).minusHours(3), // 4b length + 4b skipped, 36b offset - never used
      startOffset = BigInt(arr.toHexString(40, 40 + 4), 16), // 4b length, 40b offset - start offset
      endOffset = BigInt(arr.toHexString(44, 44 + 4), 16), // 4b length, 44b offset - end offset
      res2 = arr.slice(48, 48 + 4), //never used
      infoNum = arr.slice(52, 52 + 4), //never used
      infoTypes = arr.slice(56, 56 + 8), //never used
      infoStartTime = arr.slice(64, 64 + 4), //never used
      infoEndTime = arr.slice(68, 68 + 4), //never used
      infoStartOffset = arr.slice(72, 72 + 4), //never used
      infoEndOffset = arr.slice(76, 76 + 4) //never used
    )
  }

}
