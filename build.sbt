lazy val root = project
  .in(file("."))
  .settings(
    name := "WhitespaceEvaluator",
    version := "0.3",

    scalaVersion := "3.3.1",

    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % "test"
  )
