package dogApi

import dogApi.RandomWoof.URL
import scalaj.http.Http
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json._
import utils.ConfigType
import utils.Environment

class RandomWoof extends PhotoApi {

  override def getPhotoUrl: String = {
    val request = Http(URL)
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

object RandomWoof extends Environment("RandomWoof", ConfigType.ENVIRONMENT) {
  private lazy val URL = config.getVariableString("url")
}
