package telegram.DailyDoggo

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import database.PostgresConnector
import dogApi.ApiConnector
import telegram.RandomDoggoBot

import scala.concurrent.ExecutionContextExecutor

class DailyDoggo(randomDoggoBot: RandomDoggoBot) {
  implicit val system:       ActorSystem              = ActorSystem("DailyDoggo")
  implicit val materializer: Materializer.type        = Materializer
  implicit val ec:           ExecutionContextExecutor = system.dispatcher
  val pc  = PostgresConnector
  val api = new ApiConnector

  def run(): Unit = {
    val streamBotFlow =
      Flow[Tick]
        .via(sendDailyDoggo())
    val source = Source.actorRef(10, OverflowStrategy.dropHead)
    val ref: ActorRef = streamBotFlow.to(Sink.ignore).runWith(source)

    val schedules = List("DailyDoggo")
    Scheduler.schedule(schedules, ref, Tick)
  }

  def sendDailyDoggo(): Flow[Tick, Tick, NotUsed] = Flow[Tick].map { tick =>
    val chatIds = pc.personTable.getPeople.filter(_.subscription).map(_.chatId)
    for (chatId <- chatIds)
      randomDoggoBot.sendPhoto(chatId)
    tick
  }

}
