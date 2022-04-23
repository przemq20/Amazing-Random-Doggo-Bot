package dogApi

import scalaj.http.Http
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json._

class TheDogApi extends PhotoApi {
  private val token = ConfigGetter.theDogApiToken
  private val url   = ConfigGetter.theDogApiUrl

  override def getPhotoUrl: String = {

    val request = Http(url)
      .header("X-Auth-Token", token)
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
