package database.tables
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import database.model.Person
import doobie.implicits._
import doobie.util.transactor

class PersonTable(xa: transactor.Transactor.Aux[IO, Unit]) extends Table {

  private val dropQuery =
    sql"""
    DROP TABLE IF EXISTS person;
  """.update.run

  private val createQuery =
    sql"""
    CREATE TABLE IF NOT EXISTS person (
      chatId BIGINT UNIQUE,
      firstName VARCHAR,
      lastName VARCHAR,
      userName VARCHAR,
      counter INT,
      subscription BOOLEAN,
      PRIMARY KEY (chatId)
    )
  """.update.run

  def create(): Unit = {
    createQuery.transact(xa).unsafeRunSync()
  }

  def drop(): Unit = {
    dropQuery.transact(xa).unsafeRunSync()
  }

  def insert(chatId: Long, firstName: String, lastName: String, userName: String): Unit = {
    if (!isPersonInDb(chatId)) {
      sql"""insert into person (chatId, firstName, lastName, userName, counter, subscription)
           values ($chatId, $firstName, $lastName, $userName, 0, False)""".update.run
        .transact(xa)
        .unsafeRunSync()
    } else {
      updateCounter(chatId)
    }
  }

  private def updateCounter(chatId: Long): Unit = {
    sql"""UPDATE person SET counter = counter +1 WHERE chatid = $chatId""".update.run
      .transact(xa)
      .unsafeRunSync()
  }

  def updateSubscription(chatId: Long, sub: Boolean): Unit = {
    sql"""UPDATE person SET subscription = $sub WHERE chatid = $chatId""".update.run
      .transact(xa)
      .unsafeRunSync()
  }

  def getPeople: List[Person] = {
    sql"select chatId, firstName, lastName, userName, counter, subscription from person"
      .query[Person]
      .to[List]
      .transact(xa)
      .unsafeRunSync()
  }

  private def isPersonInDb(chatId: Long): Boolean = {
    val personList = getPeople
    personList.map(_.chatId).contains(chatId)
  }

}
