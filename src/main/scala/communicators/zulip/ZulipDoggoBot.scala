package communicators.zulip

import ZulipDoggoBot.ROOM_ID
import com.github.jamesnetherton.zulip.client.Zulip
import com.github.jamesnetherton.zulip.client.api.event.EventPoller
import com.github.jamesnetherton.zulip.client.api.event.MessageEventListener
import com.github.jamesnetherton.zulip.client.api.message.Message
import dogApi.ApiConnector
import java.lang
import utils.Environment

class ZulipDoggoBot(zulip: Zulip) {
  val api = new ApiConnector

  private val eventPoller: EventPoller = zulip
    .events()
    .captureMessageEvents(new MessageEventListener() {
      override def onEvent(t: Message): Unit = {
        val msg = t.getContent
        if (msg.contains("pjesek")) {
          val stream = t.getStream
          val topic  = t.getSubject
          scribe.info(s"Received message: $msg from ${t.getSenderFullName}. Stream: $stream Topic: $topic")
          sendPhoto(stream)
        }
      }
    })

  def start(): Unit = {
    eventPoller.start()
  }

  def sendPhoto(stream: String = ROOM_ID, topic: String = "DailyDoggo"): lang.Long = {
    val file = api.saveToFile()
    val res  = zulip.messages.fileUpload(file).execute()
    scribe.info(res)
    scribe.info(s"Sending photo to stream $stream and topic $topic")
    zulip.messages().sendStreamMessage(s"[Pjesek!]($res)", stream, topic).execute()
  }

  def sendMessage(message: String, streamName: String = ROOM_ID, topic: String = "DailyDoggo"): Unit = {
    zulip.messages().sendStreamMessage(message, streamName, topic).execute()
  }
}

object ZulipDoggoBot extends Environment("Zulip") {
  final lazy val HOST:    String = config.getVariableString("host")
  final lazy val ROOM_ID: String = config.getVariableString("room_name")
  final lazy val TOKEN:   String = config.getVariableString("token")
  final lazy val USER_ID: String = config.getVariableString("user_id")

  val zulip:   Zulip         = new Zulip(USER_ID, TOKEN, HOST)
  def apply(): ZulipDoggoBot = {
    new ZulipDoggoBot(zulip)
  }
}
