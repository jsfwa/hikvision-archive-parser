package net.jsfwa.hikvision.archive.parser

/**
  * Default archive settings
  */
trait ArchiveSettings {
  val FILE_LEN = 32 //File length in bytes
  val SEGMENT_LEN = 80 //Segment length in bytes
  val INFO_LEN = 68 //Info length in bytes
  val INDEX_LEN = 1280 //Index length in bytes
  val INFO_FILE_NAME = "info.bin"
  val INDEX_FILE_NAME = "index00.bin"
}
