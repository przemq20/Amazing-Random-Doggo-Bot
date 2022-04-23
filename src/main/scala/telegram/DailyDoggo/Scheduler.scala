package telegram.DailyDoggo

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

import java.time.LocalDateTime

object Scheduler {
  def schedule(schedules: Seq[String], ref: ActorRef, msg: AnyRef)(implicit system: ActorSystem): LocalDateTime = {
    val scheduler   = QuartzSchedulerExtension(system)
    var closestDate = LocalDateTime.MAX
    schedules.foreach(schedule => {
      val date = scheduler.schedule(schedule, ref, msg)
      if (DateTimeUtils.dateToLocalDT(date).isBefore(closestDate)) {
        closestDate = DateTimeUtils.dateToLocalDT(date)
      }
    })
    scribe.info(s"Sending on $closestDate")

    closestDate
  }
}
