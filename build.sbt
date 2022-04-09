import sbt.Keys.{libraryDependencies, scalaVersion, version}


lazy val root = (project in file(".")).
  settings(
    name := "CSE511-Project-Phase1",

    version := "0.1.0",

    scalaVersion := "2.12.15",

    organization  := "org.datasyslab",

    publishMavenStyle := true,

    mainClass := Some("cse511.SparkSQLExample")
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.2.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "3.2.0" % "provided",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "org.specs2" %% "specs2-core" % "4.15.0" % "test",
  "org.specs2" %% "specs2-junit" % "4.15.0" % "test",
  "org.slf4j" % "slf4j-api" % "1.7.5" % "test",
  "org.slf4j" % "slf4j-simple" % "1.7.5" % "test"
)