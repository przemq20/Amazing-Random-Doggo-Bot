package dogApi

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import scala.annotation.tailrec
import scala.util.Random
import scalaj.http.Http
import scalaj.http.HttpResponse
import utils.FileData

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
  final def getPhotoUrl: FileData = {
    val rand = random.nextInt(apis.length)
    val api  = apis(math.abs(rand) % apis.length)
    val url  = api.getPhotoUrl
    if (url.toLowerCase.endsWith(".jpg") || url.toLowerCase.endsWith(".png")) FileData(None, None, url, api)
    else getPhotoUrl
  }

  private def getResponse: FileData = {
    val url = getPhotoUrl
    FileData(
      None,
      Some(Http(getPhotoUrl.url).asBytes.body),
      url.url,
      getPhotoUrl.api
    )
  }

  @tailrec
  final def downloadPhoto(maxSize: Long = 1 * 1024 * 1024): FileData = {
    val response = getResponse
    val photo    = response.bytes
    if (photo.map(_.length).getOrElse(Int.MaxValue) < maxSize) FileData(None, photo, response.url, response.api)
    else downloadPhoto(maxSize)
  }

  def saveToFile(): FileData = {
    val file            = new File("temp.jpg")
    val target          = new BufferedOutputStream(new FileOutputStream(file))
    val downloadedPhoto = downloadPhoto()
    downloadedPhoto.bytes.foreach(target.write)
    target.close()
    FileData(Some(file), downloadedPhoto.bytes, downloadedPhoto.url, downloadedPhoto.api)
  }

}
