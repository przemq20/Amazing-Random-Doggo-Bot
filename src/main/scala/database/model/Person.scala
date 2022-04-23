package database.model

case class Person(
  chatId:       Long,
  firstName:    String,
  lastName:     String,
  userName:     String,
  counter:      Int,
  subscription: Boolean
)
