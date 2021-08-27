import sbt._

object Dependencies {
  lazy val cats = "org.typelevel" %% "cats-core" % "2.6.1"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "2.5.1"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.9"
  lazy val scalaMock = "org.scalamock" %% "scalamock" % "5.0.0"
  lazy val assertJ = "org.assertj" % "assertj-core" % "3.11.0"
  lazy val jUnit = "junit" % "junit" % "4.12"
}
