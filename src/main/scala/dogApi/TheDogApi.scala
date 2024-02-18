package dogApi

import dogApi.TheDogApi.TOKEN
import dogApi.TheDogApi.URL
import scalaj.http.Http
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json._
import utils.ConfigType
import utils.Environment

class TheDogApi extends PhotoApi {
  override def toString: String = "TheDogApi"

  override def getPhotoUrl: String = {

    val request = Http(URL)
      .header("X-Auth-Token", TOKEN)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8")

    scribe.info(s"$request")

    val response = request.asString.body
    scribe.info(response)

    val result = response.parseJson.asInstanceOf[JsArray].elements(0).asJsObject.fields.get("url") match {
      case Some(url) => url.convertTo[String]
      case None      => ""
    }
    result
  }
}
object TheDogApi extends Environment("TheDogApi", ConfigType.BOTH) {
  private val TOKEN = config.getVariableString("credentials.token")
  private val URL   = config.getVariableString("environment.url")
}
