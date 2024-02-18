import Rest.HttpConnection.createConnection
import communicators.rocketChat.RocketChatDoggoBot
import communicators.telegram.TelegramDoggoBot
import communicators.zulip.ZulipDoggoBot
import scala.annotation.tailrec
import utils.Bot
import utils.BotType
import utils.ConfigReader

object Main extends App {
  val config = new ConfigReader("RandomDoggo")
  private val botsToRun: List[BotType] = config.getVariableList("Bots").map(BotType.fromString)

  val startedBots = startBots(botsToRun)
  println(startedBots)

  createConnection(startedBots)

  @tailrec
  private def startBots(botsToRun: List[BotType], runningBots: List[Bot] = List.empty): List[Bot] = {
    botsToRun match {
      case _ if botsToRun.isEmpty => runningBots
      case _                      =>
        val bot = botsToRun.head
        bot match {
          case BotType.ZULIP      =>
            val zulipBot = ZulipDoggoBot()
            zulipBot.run()
            startBots(botsToRun.drop(1), runningBots ::: zulipBot.asInstanceOf[Bot] :: Nil)
          case BotType.TELEGRAM   =>
            val telegramBot = new TelegramDoggoBot
            telegramBot.run()
            startBots(botsToRun.drop(1), runningBots ::: telegramBot.asInstanceOf[Bot] :: Nil)
          case BotType.ROCKETCHAT =>
            val rocketBot = new RocketChatDoggoBot
            rocketBot.run()
            startBots(botsToRun.drop(1), runningBots ::: rocketBot.asInstanceOf[Bot] :: Nil)
        }
    }
  }
}
