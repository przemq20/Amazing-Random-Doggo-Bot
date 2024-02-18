package communicators.telegram

import TelegramDoggoBot.ADMIN_ROOM
import TelegramDoggoBot.TOKEN
import akka.actor.ActorSystem
import com.bot4s.telegram.api.ChatActions
import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.clients.AkkaHttpClient
import com.bot4s.telegram.future.Polling
import com.bot4s.telegram.future.TelegramBot
import com.bot4s.telegram.methods.SendMessage
import com.bot4s.telegram.methods.SendPhoto
import com.bot4s.telegram.models.InputFile
import com.bot4s.telegram.models.Message
import database.PostgresConnector
import dogApi.ApiConnector
import scala.concurrent.Future
import scala.language.postfixOps
import utils.Bot
import utils.Environment

class TelegramDoggoBot extends TelegramBot with Polling with Commands[Future] with ChatActions[Future] with Bot {

  implicit val actorSystem: ActorSystem            = ActorSystem()
  override val client:      RequestHandler[Future] = new AkkaHttpClient(TOKEN)

  val api               = new ApiConnector
  val pc                = new PostgresConnector()
  private val adminChat = ADMIN_ROOM

  onCommand("pjesek".or("piesek")) { implicit msg =>
    val chat = msg.chat
    for {
      _ <- Future {
             uploadingPhoto
             sendPhoto(msg.source)
           }
      _ <- Future {
             val firstName = msg.chat.firstName.getOrElse("")
             val lastName  = msg.chat.lastName.getOrElse("")
             scribe.info(s"Updating counter for $firstName $lastName")
             if (chat.id != adminChat)
               sendMessage(
                 adminChat,
                 s"$firstName $lastName downloaded a photo",
                 disableNotification = Some(true)
               )
             pc.personTable
               .insert(chat.id, firstName, lastName, chat.username.getOrElse(""))
           }
    } yield ()

  }

  onCommand("subscribe") { implicit msg =>
    Future {
      scribe.info(s"Updating subscription for ${msg.chat.firstName} ${msg.chat.lastName}")
      pc.personTable.updateSubscription(msg.chat.id, sub = true)
      sendMessage(msg.chat.id, "Successfully subscribed")
    }
  }

  onCommand("unsubscribe") { implicit msg =>
    Future {
      scribe.info(s"Updating subscription for ${msg.chat.firstName} ${msg.chat.lastName}")
      pc.personTable.updateSubscription(msg.chat.id, sub = false)
      sendMessage(msg.chat.id, "Successfully unsubscribed")
    }
  }

  def sendPhoto(chatId: Long, caption: Option[String] = None): Future[Message] = {
    val bytes = api.downloadPhoto(1 * 1024 * 1024).bytes.getOrElse(Array[Byte]())
    val photo = InputFile("pjesek.png", bytes)
    try {
      request(SendPhoto(chatId, photo, caption))
    } catch {
      case e: Throwable =>
        scribe.error(e.getLocalizedMessage)
        e.printStackTrace()
        Future.failed(e)
    }
  }

  def sendMessage(chatId: Long, message: String, disableNotification: Option[Boolean] = None): Future[Message] = {
    try {
      request(SendMessage(chatId, message, disableNotification = disableNotification))
    } catch {
      case e: Throwable =>
        scribe.error(e.getLocalizedMessage)
        e.printStackTrace()
        Future.failed(e)
    }
  }

  override def runCron(): Unit = {
    val cron = new TelegramDailyDoggo(this)
    cron.run()
  }

  override def run(): Future[Unit] = {
    Future(runCron())
    super.run()
  }

}

object TelegramDoggoBot extends Environment("Telegram") {
  final val TOKEN:      String = config.getVariableString("token")
  final val ADMIN_ROOM: Int    = config.getVariableInt("admin_room")
}
