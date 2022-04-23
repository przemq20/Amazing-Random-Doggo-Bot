import Rest.HttpConnection.createConnection
import telegram.DailyDoggo.DailyDoggo
import telegram.{ Credentials, RandomDoggoBot }

object Main extends App {
  val TOKEN = Credentials.token
  val bot   = new RandomDoggoBot(TOKEN)

  new Thread {
    override def run(): Unit = bot.run()
  }.start()

  new Thread {
    override def run(): Unit = createConnection
  }.start()

  new Thread {
    val dailyDoggo = new DailyDoggo(bot)
    override def run(): Unit = dailyDoggo.run()
  }.start()

}
