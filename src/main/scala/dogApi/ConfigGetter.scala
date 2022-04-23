package dogApi

import com.typesafe.config.{ Config, ConfigFactory }

import scala.jdk.CollectionConverters._

object ConfigGetter {
  val config: Config = ConfigFactory.load()

  val theDogApiToken: String = config
    .getConfig("RandomDoggo.TheDogApi.credentials")
    .getString("token")

  val theDogApiUrl: String = config
    .getConfig("RandomDoggo.TheDogApi.environment")
    .getString("url")

  val dogCeoCoreUrl: String = config
    .getConfig("RandomDoggo.DogCeo.environment")
    .getString("url")

  val dogCeoEndpoint: String = config
    .getConfig("RandomDoggo.DogCeo.environment")
    .getString("randomImage")

  val dogCeoBreeds: List[String] = config
    .getConfig("RandomDoggo.DogCeo.environment")
    .getList("breeds")
    .unwrapped()
    .asScala
    .map(_.toString)
    .toList
}
