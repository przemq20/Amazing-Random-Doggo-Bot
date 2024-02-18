package communicators.rocketChat

import RocketChatDoggoBot.ROOM_ID
import RocketChatDoggoBot.SEND_MESSAGE_URL
import RocketChatDoggoBot.SEND_PHOTO_URL
import RocketChatDoggoBot.TOKEN
import RocketChatDoggoBot.UPLOAD_PHOTO_URL
import RocketChatDoggoBot.USER_ID
import akka.http.scaladsl.model.DateTime
import dogApi.ApiConnector

import java.nio.file.Files
import java.nio.file.Paths
import scalaj.http.Http
import scalaj.http.HttpOptions
import scalaj.http.MultiPart
import utils.Bot
import utils.Environment

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RocketChatDoggoBot extends Bot {
  def sendMessage(message: String): Unit = {
    val data = makeMessageString(message)
    scribe.info(
      s"""Sending
         |$data
         |""".stripMargin
    )
    try {
      val request = Http(SEND_MESSAGE_URL)
        .postData(data)
        .header("X-Auth-Token", TOKEN)
        .header("X-User-Id", USER_ID)
        .header("Content-type", "application/json")
        .header("Charset", "UTF-8")

      scribe.info(s"$request")
      val response = request.option(HttpOptions.readTimeout(10000)).asString
      scribe.info(s"$response")
    } catch {
      case e: Throwable =>
        scribe.error(s"Error: ${e.getLocalizedMessage}")
        e.printStackTrace()
    }
  }

  def sendPhoto(): Unit = {
    val api      = new ApiConnector
    val photoUrl = api.getPhotoUrl
    val data     = makePhotoString(photoUrl.url)
    try {

      val request = Http(SEND_PHOTO_URL)
        .postData(data)
        .header("X-Auth-Token", TOKEN)
        .header("X-User-Id", USER_ID)
        .header("Content-type", "application/json")
        .header("Charset", "UTF-8")

      scribe.info(s"$request")
      val response = request.option(HttpOptions.readTimeout(10000)).asString
      scribe.info(s"$response")
    } catch {
      case e: Throwable =>
        scribe.error(s"Error: ${e.getLocalizedMessage}")
        e.printStackTrace()
    }
  }

  def uploadPhoto(): Unit = {
    val api   = new ApiConnector
    val photo = api.saveToFile()
    try {
      val bytes: Array[Byte] = Files.readAllBytes(Paths.get(photo.file.get.getAbsolutePath))
      val fileName = s"${photo.api.toString}-${DateTime.now}"
      val filePart = MultiPart("file", fileName, "image/jpeg", bytes)

      val request = Http(UPLOAD_PHOTO_URL)
        .method("POST")
        .header("X-Auth-Token", TOKEN)
        .header("X-User-Id", USER_ID)
        .postMulti(filePart)

      scribe.info(s"$request")
      val response = request.option(HttpOptions.readTimeout(10000)).asString
      scribe.info(s"$response")
    } catch {
      case e: Throwable =>
        scribe.error(s"Error: ${e.getLocalizedMessage}")
        e.printStackTrace()
    }
  }

  private def makeMessageString(content: String): String = {
    val newlineChar    = "\\n"
    val whitespaceChar = "\u2001"
    val rawContent     = content
      .replace("\n", newlineChar)
      .replace("\t", whitespaceChar * 3)
      .replace("\"", "")
      .replace("{", "")
      .replace("}", "")

    s"""{"message": {"rid": "$ROOM_ID", "msg": "$rawContent "}}"""
  }

  private def makePhotoString(photoUrl: String): String = {
    s"""{"roomId": "$ROOM_ID", "text": "Pjesek!", "attachments": [{"image_url": "$photoUrl", "thumb_url": "$photoUrl"}] }"""
  }

  private def makeUploadString(photoPath: String = "temp.jpg"): String = {
    s"""{"file": "$photoPath", "text": "Pjesek!"}"""
  }

  override def runCron(): Unit = {
    val cron = new RocketHourlyDoggo(this)
    cron.run()
  }

  override def run(): Future[Unit] = {
    Future(runCron())
  }
}

object RocketChatDoggoBot extends Environment("RocketChat") {
  final lazy val HOST:     String = config.getVariableString("host")
  final lazy val ROOM_ID:  String = config.getVariableString("room_id")
  final lazy val API_PATH: String = "/api/v1"

  final lazy val CORE_URL: String = s"$HOST$API_PATH"
  final lazy val SEND_MESSAGE_URL = s"$CORE_URL/chat.sendMessage"
  final lazy val SEND_PHOTO_URL   = s"$CORE_URL/chat.postMessage"
  final lazy val UPLOAD_PHOTO_URL = s"$CORE_URL/rooms.upload/$ROOM_ID"
  final lazy val ROOMS_INFO       = s"$CORE_URL/rooms.info?roomId=$ROOM_ID"

  final lazy val TOKEN:   String = config.getVariableString("token")
  final lazy val USER_ID: String = config.getVariableString("user_id")
  final lazy val AVATAR:  String = config.getVariableString("avatar")
}
