package telegram

import com.typesafe.config.{Config, ConfigFactory}

object Credentials {
  val config: Config = ConfigFactory.load()

  val token: String = config
    .getConfig("RandomDoggo.Telegram.credentials")
    .getString("token")
}
