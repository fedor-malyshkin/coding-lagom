ThisBuild / organization := "coding.lagom"
ThisBuild / version := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
ThisBuild / scalaVersion := "2.13.6"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % Test

lazy val `coding-lagom` = (project in file("."))
  .aggregate(`coding-lagom-api`, `coding-lagom-impl`, `coding-lagom-stream-api`, `coding-lagom-stream-impl`)

lazy val `coding-lagom-api` = (project in file("coding-lagom-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `coding-lagom-impl` = (project in file("coding-lagom-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`coding-lagom-api`)

lazy val `coding-lagom-stream-api` = (project in file("coding-lagom-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `coding-lagom-stream-impl` = (project in file("coding-lagom-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`coding-lagom-stream-api`, `coding-lagom-api`)
