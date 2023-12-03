name := "RandomDoggoTelegram"

version := "0.1"

enablePlugins(JavaAppPackaging)

scalaVersion := "2.13.8"

val AkkaVersion     = "2.8.0"
val AkkaHttpVersion = "10.5.0"

libraryDependencies ++= Seq(
  "com.bot4s" %% "telegram-core" % "5.6.3",
  "com.bot4s" %% "telegram-akka" % "5.6.3",
  "org.typelevel" %% "cats-core" % "2.9.0",
  "io.spray" %% "spray-json" % "1.3.6",
  "com.outr" %% "scribe" % "3.11.2",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.4-akka-2.6.x",
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "net.coobird" % "thumbnailator" % "0.4.19",
  "com.github.jamesnetherton" % "zulip-java-client" % "0.5.2"
)
