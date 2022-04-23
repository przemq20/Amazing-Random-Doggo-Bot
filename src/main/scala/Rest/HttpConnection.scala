package Rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.Directives.{ complete, get }

import scala.concurrent.Future

object HttpConnection {
  def createConnection: Future[Http.ServerBinding] = {
    val route = get {
      complete(
        HttpEntity(
          ContentTypes.`text/html(UTF-8)`, {
            "hello"
          }
        )
      )
    }

    implicit val system: ActorSystem = ActorSystem("Server")

    val host = "0.0.0.0"
    val port: Int = sys.env.getOrElse("PORT", "8080").toInt

    Http().newServerAt(host, port).bindFlow(route)
  }
}
