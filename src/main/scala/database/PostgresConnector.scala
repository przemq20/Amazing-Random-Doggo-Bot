package database

import cats.effect.IO
import com.typesafe.config.{ Config, ConfigFactory }
import database.tables.PersonTable
import doobie.Transactor

object PostgresConnector {

  private val config: Config = ConfigFactory.load().getConfig("RandomDoggo.PostgreSQL.credentials")

  private val url: String = config
    .getString("URL")
  private val user: String = config
    .getString("user")
  private val pass: String = config
    .getString("pass")

  private val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", // driver classname
    url,                     // connect URL (driver-specific)
    user,                    // user
    pass                     // password
  )

  val personTable = new PersonTable(xa)
}
