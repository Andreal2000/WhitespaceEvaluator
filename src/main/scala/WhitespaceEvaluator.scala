import scala.collection.mutable.{Map, Stack}
import scala.io.StdIn.{readInt, readLine}
import scala.util.parsing.combinator.JavaTokenParsers

object WhitespaceEvaluator {
  def main(args: Array[String]): Unit = {
    args.foreach { file =>
      val p = new WhitespaceParser
      val source = scala.io.Source.fromFile(file)
      val input = source.mkString
      p.parseAll(p.program, input) match {
        case p.Success(result, _) => p.run(result)
        case x => println(x.toString)
      }
    }
  }
}
