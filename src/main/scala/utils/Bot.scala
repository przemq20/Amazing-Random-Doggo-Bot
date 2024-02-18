package utils

import scala.concurrent.Future

trait Bot {
  def runCron(): Unit

  def run(): Future[Unit]
}
