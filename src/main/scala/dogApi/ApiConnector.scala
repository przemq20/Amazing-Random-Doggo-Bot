package dogApi

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import scala.annotation.tailrec
import scala.util.Random
import scalaj.http.Http
import scalaj.http.HttpResponse

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

  @tailrec
  final def getPhotoUrl: String = {
    val url = apis(math.abs(random.nextInt()) % apis.length).getPhotoUrl
    if (url.toLowerCase.endsWith(".jpg") || url.toLowerCase.endsWith(".png")) url
    else getPhotoUrl
  }

  private def getResponse: HttpResponse[Array[Byte]] = {
    Http(getPhotoUrl).asBytes
  }

  @tailrec
  final def downloadPhoto(maxSize: Long = 1 * 1024 * 1024): Array[Byte] = {
    val photo = getResponse.body
    if (photo.length < maxSize) photo
    else downloadPhoto(maxSize)
  }

  def saveToFile(): File = {
    val file   = new File("temp.jpg")
    val target = new BufferedOutputStream(new FileOutputStream(file))
    downloadPhoto().foreach(target.write(_))
    target.close()
    file
  }

}
