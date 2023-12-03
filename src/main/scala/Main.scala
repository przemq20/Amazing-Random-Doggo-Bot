import Rest.HttpConnection.createConnection
import communicators.rocketChat.RocketChatDoggoBot
import communicators.rocketChat.RocketHourlyDoggo
import communicators.telegram.TelegramDailyDoggo
import communicators.telegram.TelegramDoggoBot
import communicators.zulip.ZulipDoggoBot
import communicators.zulip.ZulipHourlyDoggo
import utils.BotType
import utils.ConfigReader

object Main extends App {
  val config = new ConfigReader("RandomDoggo")
  private val botsToRun: List[BotType] = config.getVariableList("Bots").map(BotType.fromString)

  new Thread {
    createConnection
  }.start()

  for (bot <- botsToRun) {
    new Thread {
      bot match {
        case BotType.ZULIP      =>
          val zulipBot = ZulipDoggoBot()
          new ZulipHourlyDoggo(zulipBot).run()
          zulipBot.start()
        case BotType.TELEGRAM   =>
          val telegramBot = new TelegramDoggoBot
          new TelegramDailyDoggo(telegramBot).run()
          telegramBot.run()
        case BotType.ROCKETCHAT =>
          val rocketBot = new RocketChatDoggoBot
          new RocketHourlyDoggo(rocketBot).run()
      }
    }.start()
  }
}
