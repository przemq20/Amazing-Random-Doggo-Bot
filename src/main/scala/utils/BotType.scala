package utils

sealed trait BotType {}
object BotType {
  case object ZULIP extends BotType
  case object TELEGRAM extends BotType
  case object ROCKETCHAT extends BotType

  def fromString(str: String): BotType = {
    str match {
      case "Zulip"      => ZULIP
      case "Rocket" => ROCKETCHAT
      case "Telegram"   => TELEGRAM
      case _            => throw new Exception("")
    }
  }
}
