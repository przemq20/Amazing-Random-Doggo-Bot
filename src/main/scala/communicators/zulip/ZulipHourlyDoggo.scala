package communicators.zulip

import akka.{ Done, NotUsed }
import akka.actor.{ ActorRef, ActorSystem }
import akka.stream.scaladsl.{ Flow, Sink, Source }
import akka.stream.{ CompletionStrategy, Materializer, OverflowStrategy }
import database.PostgresConnector
import dogApi.ApiConnector
import utils.scheduler.{ Scheduler, Tick }

import scala.concurrent.ExecutionContextExecutor

class ZulipHourlyDoggo(zulipBot: ZulipDoggoBot) {
  implicit val system:       ActorSystem              = ActorSystem("ZulipDailyDoggo")
  implicit val materializer: Materializer.type        = Materializer
  implicit val ec:           ExecutionContextExecutor = system.dispatcher
  val pc  = new PostgresConnector()
  val api = new ApiConnector

  def run(): Unit = {
    val streamBotFlow =
      Flow[Tick]
        .via(sendDailyDoggo())
    val source = Source.actorRef(
      completionMatcher = {
        case Done =>
          CompletionStrategy.immediately
      },
      failureMatcher   = PartialFunction.empty,
      bufferSize       = 10,
      overflowStrategy = OverflowStrategy.dropHead
    )
    val ref: ActorRef = streamBotFlow.to(Sink.ignore).runWith(source)

    val schedules = List("ZulipCron")
    Scheduler.schedule(schedules, ref, Tick)
  }

  private def sendDailyDoggo(): Flow[Tick, Tick, NotUsed] = Flow[Tick].map { tick =>
    zulipBot.sendPhoto()
    tick
  }

}
