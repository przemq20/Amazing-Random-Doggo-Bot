name := "RandomDoggoTelegram"

version := "0.1"

enablePlugins(JavaAppPackaging)

scalaVersion := "2.13.8"

val AkkaVersion = "2.6.13"
val AkkaHttpVersion = "10.2.4"

libraryDependencies ++= Seq(
  "com.bot4s" %% "telegram-core" % "5.0.0",
  "com.bot4s" %% "telegram-akka" % "5.0.0",
  "org.typelevel" %% "cats-core" % "2.2.0",
  "io.spray" %% "spray-json" % "1.3.5",
  "com.outr" %% "scribe" % "3.5.3",
  "org.tpolecat" %% "doobie-core"      % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC1",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.4-akka-2.6.x",
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "net.coobird" % "thumbnailator" % "0.4.17"
)