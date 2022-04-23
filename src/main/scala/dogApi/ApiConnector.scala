package dogApi

import scalaj.http.{ Http, HttpResponse }

import scala.annotation.tailrec
import scala.util.Random

class ApiConnector {

  private val dogCeo     = new DogCeo
  private val theDogApi  = new TheDogApi
  private val randomWoof = new RandomWoof

  private val apis: List[PhotoApi] = List(
    dogCeo,
    theDogApi,
    randomWoof
  )

  private val random = new Random()

  private def getPhotoUrl: String = {
    apis(math.abs(random.nextInt()) % apis.length).getPhotoUrl
  }

  private def getResponse: HttpResponse[Array[Byte]] = {
    Http(getPhotoUrl).asBytes
  }

  @tailrec
  final def downloadPhoto(maxSize: Long): Array[Byte] = {
    val photo = getResponse.body
    if (photo.length < maxSize) photo
    else downloadPhoto(maxSize)
  }

}
