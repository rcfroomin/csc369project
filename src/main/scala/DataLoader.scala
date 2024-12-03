import scala.io.Source

object DataLoader {
  def loadData(path: String): Seq[Map[String, String]] = {
    val lines = Source.fromFile(path).getLines().toList
    val headers = lines.head.split(",").map(_.trim)
    lines.tail.map { line =>
      headers.zip(line.split(",").map(_.trim)).toMap
    }
  }
}
