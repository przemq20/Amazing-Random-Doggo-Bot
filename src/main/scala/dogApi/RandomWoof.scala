package dogApi

import scalaj.http.Http
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json._
import utils.{ConfigType, Environment}

class RandomWoof extends PhotoApi {
  private val url = "https://random.dog/woof.json"

  override def getPhotoUrl: String = {
    val request = Http(url)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8")

    scribe.info(s"$request")

    val response = request.asString.body
    scribe.info(response)

    val result = response.parseJson.asJsObject.fields.get("url") match {
      case Some(url) => url.convertTo[String]
      case None      => ""
    }
    result
  }
}

object RandomWoof extends Environment("RandomWoof", ConfigType.ENVIRONMENT){
  private lazy val URL = config.getVariableString("url")
}