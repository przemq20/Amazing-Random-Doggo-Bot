package utils

sealed trait ConfigType {}
object ConfigType {
  case object CREDENTIALS extends ConfigType
  case object ENVIRONMENT extends ConfigType
  case object BOTH extends ConfigType
}
