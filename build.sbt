lazy val root = project
  .in(file("."))
  .settings(
    name := "WhitespaceEvaluator",
    version := "0.1.0",

    scalaVersion := "3.3.1",

    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0"
  )
