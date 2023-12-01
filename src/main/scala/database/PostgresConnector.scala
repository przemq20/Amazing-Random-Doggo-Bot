package database

import cats.effect.IO
import database.PostgresConnector.{PASSWORD, URL, USERNAME}
import database.tables.PersonTable
import doobie.Transactor
import utils.Environment

class PostgresConnector {

  private val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    URL,
    USERNAME,
    PASSWORD
  )

  val personTable = new PersonTable(xa)
}
object PostgresConnector extends Environment("PostgresSql") {
  final lazy val URL:      String = config.getVariableString("URL")
  final lazy val USERNAME: String = config.getVariableString("user")
  final lazy val PASSWORD: String = config.getVariableString("pass")
}
