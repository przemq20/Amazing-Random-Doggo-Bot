package telegram

import akka.actor.ActorSystem
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.api.{ ChatActions, RequestHandler }
import com.bot4s.telegram.clients.AkkaHttpClient
import com.bot4s.telegram.future.{ Polling, TelegramBot }
import com.bot4s.telegram.methods.{ SendMessage, SendPhoto }
import com.bot4s.telegram.models.{ InputFile, Message }
import database.PostgresConnector
import dogApi.ApiConnector

import scala.concurrent.Future
import scala.language.postfixOps

class RandomDoggoBot(val token: String) extends TelegramBot with Polling with Commands[Future] with ChatActions[Future] {

  implicit val actorSystem: ActorSystem            = ActorSystem()
  override val client:      RequestHandler[Future] = new AkkaHttpClient(token)

  val api               = new ApiConnector
  val pc                = PostgresConnector
  private val adminChat = 1016687872

  onCommand("pjesek".or("piesek")) { implicit msg =>
    val chat = msg.chat
    for {
      _ <- Future {
            uploadingPhoto
            sendPhoto(msg.source)
          }
      _ <- Future {
            scribe.info(s"Updating counter for ${msg.chat.firstName} ${msg.chat.lastName}")
            if (chat.id != adminChat)
              sendMessage(
                adminChat,
                s"${chat.firstName.getOrElse("")} ${chat.lastName.getOrElse("")} downloaded a photo",
                disableNotification = Some(true)
              )
            pc.personTable
              .insert(chat.id, chat.firstName.getOrElse(""), chat.lastName.getOrElse(""), chat.username.getOrElse(""))
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

  def sendPhoto(chatId: Long): Future[Message] = {
    val bytes = api.downloadPhoto(1 * 1024 * 1024)
    val photo = InputFile("pjesek.png", bytes)
    request(SendPhoto(chatId, photo))
  }

  def sendMessage(chatId: Long, message: String, disableNotification: Option[Boolean] = None): Future[Message] =
    request(SendMessage(chatId, message, disableNotification = disableNotification))
}
