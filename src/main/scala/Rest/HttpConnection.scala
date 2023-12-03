package Rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Directives.get
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.pathEndOrSingleSlash
import dogApi.ApiConnector
import scala.concurrent.Future

object HttpConnection {
  val api = new ApiConnector
  def createConnection: Future[Http.ServerBinding] = {
    val route = {
      get {
        scribe.info("Received GET request")
        pathEndOrSingleSlash {
          complete(
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`, {
                "hello"
              }
            )
          )
        } ~
          path("pjesek") {
            val photo = api.getPhotoUrl
            complete(
              HttpEntity(
                ContentTypes.`text/html(UTF-8)`, {
                  s"""
                     |<img src="$photo" alt="Pjesek!">
                     |""".stripMargin
                }
              )
            )
          }
      }
    }

    implicit val system: ActorSystem = ActorSystem("Server")

    val host = "0.0.0.0"
    val port: Int = sys.env.getOrElse("PORT", "8080").toInt

    Http().newServerAt(host, port).bindFlow(route)
  }
}
