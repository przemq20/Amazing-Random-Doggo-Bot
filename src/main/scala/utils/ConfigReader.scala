package utils

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import scala.jdk.CollectionConverters._
import scala.util.Properties

class ConfigReader(configPath: String) {
  private val env              = Properties.envOrElse("env", "local")
  private val resourceBasename = s"application-$env.conf"
  scribe.info(s"Reading config: $resourceBasename for $configPath")
  private final val config: Config = ConfigFactory
    .load(resourceBasename)
    .getConfig(configPath)

  def getVariableString(variable: String): String = {
    val configVal = Properties.envOrElse(variable, config.getString(variable))
    scribe.info(s"Reading variable $variable from config $configPath")
    configVal
  }

  def getVariableInt(variable: String): Int = getVariableString(variable).toInt

  def getVariableList(variable: String): List[String] = {
    Properties.envOrElse(variable, config.getString(variable)).split(";").toList
//      config.getStringList(variable).asScala.toList
  }
}
