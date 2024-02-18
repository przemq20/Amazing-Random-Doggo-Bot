package communicators.rocketChat

import akka.Done
import akka.NotUsed
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.stream.CompletionStrategy
import akka.stream.Materializer
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import database.PostgresConnector
import dogApi.ApiConnector
import scala.concurrent.ExecutionContextExecutor
import utils.scheduler.Scheduler
import utils.scheduler.Tick

class RocketHourlyDoggo(rocketBot: RocketChatDoggoBot) {
  implicit val system:       ActorSystem              = ActorSystem("RocketDailyDoggo")
  implicit val materializer: Materializer.type        = Materializer
  implicit val ec:           ExecutionContextExecutor = system.dispatcher
  val pc  = new PostgresConnector()
  val api = new ApiConnector

  def run(): Unit = {
    val streamBotFlow =
      Flow[Tick]
        .via(sendDailyDoggo())
    val source        = Source.actorRef(
      completionMatcher = {
        case Done =>
          CompletionStrategy.immediately
      },
      failureMatcher    = PartialFunction.empty,
      bufferSize        = 10,
      overflowStrategy  = OverflowStrategy.dropHead
    )
    val ref: ActorRef = streamBotFlow.to(Sink.ignore).runWith(source)

    val schedules = List("RocketCron")
    Scheduler.schedule(schedules, ref, Tick)
  }

  private def sendDailyDoggo(): Flow[Tick, Tick, NotUsed] = Flow[Tick].map { tick =>
    rocketBot.uploadPhoto()
    tick
  }

}
