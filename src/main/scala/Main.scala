import Rest.HttpConnection.createConnection
import communicators.rocketChat.{ RocketChatDoggoBot, RocketHourlyDoggo }
import communicators.telegram.{ TelegramDailyDoggo, TelegramDoggoBot }
import communicators.zulip.{ ZulipDoggoBot, ZulipHourlyDoggo }

object Main extends App {
  val telegramBot = new TelegramDoggoBot
  val zulipBot    = ZulipDoggoBot()
  val rocketBot   = new RocketChatDoggoBot

  new Thread {
    new TelegramDailyDoggo(telegramBot).run()
    telegramBot.run()
  }

  new Thread {
    new ZulipHourlyDoggo(zulipBot).run()
    zulipBot.start()
  }.start()

  new Thread {
    createConnection
  }

  new Thread {
    new RocketHourlyDoggo(rocketBot).run()
  }.start()

}
