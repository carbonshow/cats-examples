version := "0.1"

val circeVersion = "0.12.3"
val catsVersion = "2.0.0"

lazy val commonSettings = Seq(scalaVersion := "2.13.2",
  libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.1.2" % "test"))

lazy val circeProject = Project("circe", file("circe"))
  .settings(commonSettings)
  .settings(
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)
)

lazy val catsProject = Project("cats", file("cats"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core",
      "org.typelevel" %% "cats-kernel",
      "org.typelevel" %% "cats-macros"
    ).map(_ % catsVersion)
  )

lazy val rootProject = Project("demo-root", file("."))
  .settings(commonSettings)
  .aggregate(circeProject, catsProject)
