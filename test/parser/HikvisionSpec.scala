import akka.stream.ActorMaterializer
import net.jsfwa.hikvision.archive.parser._
import org.joda.time
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class HikvisionSpec extends FlatSpec with BeforeAndAfter with Matchers {

  var hInfo: HInfo = _

  before {
    val t = DateTime.now().getMillis
    hInfo = (new DefaultArchiveParser("testdata\\") with ArchiveSettings).parse()
    println("Time: " + (DateTime.now().getMillis - t))
  }

  "InfoParser" should "provide all file data" in {

    println(hInfo)

    assert(hInfo.indices.nonEmpty)
  }

  "Segment list" should "display elements by date" in {

    val date = new DateTime(2018, 1, 13, 0, 0)

    val segments = hInfo.indices.map(_.files.find(
      f =>
        f.startTime.getMillis <= date.getMillis && f.endTime.getMillis >= date.getMillis
    ))

    println(segments)

    assert(segments.nonEmpty)
  }

  "Days list" should "display days of month with records" in {
    val date = new DateTime()

    val days = hInfo.indices.map(_.files.filter(_.startTime.getMonthOfYear <= date.getMonthOfYear).
      map(_.startTime.getDayOfMonth)
    ).foldLeft(List[Int]())(_ ++ _).distinct

    println(days)

    assert(days.nonEmpty)
  }
}