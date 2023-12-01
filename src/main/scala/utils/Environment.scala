package utils

class Environment(name: String, configType: ConfigType = ConfigType.CREDENTIALS) {
  scribe.info(s"Initializing config for $name")
  final def config: ConfigReader = configType match {
    case ConfigType.CREDENTIALS => new ConfigReader(s"RandomDoggo.$name.credentials")
    case ConfigType.ENVIRONMENT => new ConfigReader(s"RandomDoggo.$name.environment")
    case ConfigType.BOTH        => new ConfigReader(s"RandomDoggo.$name")
  }
}
