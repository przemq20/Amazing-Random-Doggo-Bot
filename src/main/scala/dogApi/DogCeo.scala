package dogApi

import dogApi.DogCeo.BREEDS
import dogApi.DogCeo.ENDPOINT
import dogApi.DogCeo.URL
import scala.util.Random
import scalaj.http.Http
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json._
import utils.ConfigType
import utils.Environment

class DogCeo extends PhotoApi {
  private val random: Random = new Random()

  override def getPhotoUrl: String = {
    val randomBreed: String = BREEDS(random.nextInt(BREEDS.size))
    val fullUrl = s"$URL$randomBreed$ENDPOINT"
    val request = Http(fullUrl)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8")

    scribe.info(s"$request")

    val response = request.asString.body
    scribe.info(response)

    val result = response.parseJson.asJsObject.fields.get("message") match {
      case Some(url) => url.convertTo[String]
      case None      => ""
    }
    result
  }
}

object DogCeo extends Environment("DogCeo", ConfigType.ENVIRONMENT) {
  private lazy val URL:      String       = config.getVariableString("url")
  private lazy val ENDPOINT: String       = config.getVariableString("randomImage")
  private lazy val BREEDS:   List[String] = config.getVariableList("breeds")
}
