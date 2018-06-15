package net.jsfwa.hikvision.archive.parser

import java.io.{BufferedInputStream, FileInputStream}


/**
  * Created by Andrei Zubrilin, 2018
  */
trait ArchiveParser {

  def parse() : HInfo
}


class DefaultArchiveParser(dir: String) extends ArchiveParser{
  self: ArchiveSettings =>

  override def parse(): HInfo = {
    new DefaultInfoParser(dir, self).parse()
  }
}