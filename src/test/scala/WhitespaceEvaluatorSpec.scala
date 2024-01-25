import org.scalatest.Assertion
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.io.{ByteArrayOutputStream, StringReader}
import java.nio.file.{Files, Paths}
import scala.io.StdIn

class WhitespaceEvaluatorSpec extends AnyFlatSpec with should.Matchers {
  val resources = "./src/test/scala/resources/"

  def testFile(fileName: String): Assertion = {
    val path = s"$resources/$fileName/$fileName"
    val sourcePath = s"$path.ws"
    val inputPath = s"$path.in"
    val outputPath = s"$path.out"

    val inputStr = if Files.exists(Paths.get(inputPath)) then {
      val inputFile = scala.io.Source.fromFile(inputPath)
      inputFile.mkString
    } else ""

    val outputFile = scala.io.Source.fromFile(outputPath)
    val outputStr = outputFile.mkString

    val in = new StringReader(inputStr)
    val out = new ByteArrayOutputStream()

    Console.withOut(out) {
      Console.withIn(in) {
        WhitespaceEvaluator.main(Array(sourcePath))
      }
    }
    out.toString shouldBe outputStr
  }

  behavior of "WhitespaceEvaluator"

  it should "run calc" in testFile("calc")

  it should "run count" in testFile("count")

  it should "run fibonacci" in testFile("fibonacci")

  it should "run hanoi" in testFile("hanoi")

  it should "run hworld" in testFile("hworld")

  it should "run name" in testFile("name")

  it should "run quine" in testFile("quine")

  it should "run quine-2" in testFile("quine-2")

  it should "run vm-bf" in testFile("vm-bf")

  it should "run vm-mal" in testFile("vm-mal")
}
