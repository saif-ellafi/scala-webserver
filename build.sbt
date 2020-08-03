val Http4sVersion = "0.21.6"
val CirceVersion = "0.13.0"
val Specs2Version = "4.9.0"
val LogbackVersion = "1.2.3"

lazy val http4sDependencies = Seq(
  "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s"      %% "http4s-circe"        % Http4sVersion,
  "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
  "io.circe"        %% "circe-generic"       % CirceVersion,
  "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
)

lazy val dbDependencies = Seq(
  "com.typesafe.slick" %% "slick" % "3.3.1",
  "org.slf4j" % "slf4j-nop" % "1.7.30",
  "com.h2database" % "h2" % "1.4.200",
  "mysql" % "mysql-connector-java" % "8.0.21",
  "org.scalatest" %% "scalatest" % "3.2.0" % "test"
)

lazy val root = (project in file("."))
  .settings(
    organization := "org.simple",
    name := "simple-webserver",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.11",
    libraryDependencies ++= http4sDependencies ++ dbDependencies,
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
  "-language:implicitConversions"
)

fork in run := true

mainClass in run := Some("org.simple.runtime.Main")