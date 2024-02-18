package utils.scheduler

import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Scheduler {
  def schedule(schedules: Seq[String], ref: ActorRef, msg: AnyRef)(implicit system: ActorSystem): LocalDateTime = {
    val scheduler   = QuartzSchedulerExtension(system)
    var closestDate = LocalDateTime.MAX
    val format = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")

    schedules.foreach(schedule => {
      val date = scheduler.schedule(schedule, ref, msg)
      if (DateTimeUtils.dateToLocalDT(date).isBefore(closestDate)) {
        closestDate = DateTimeUtils.dateToLocalDT(date)
      }
    })
    scribe.info(s"Sending on ${closestDate.format(format)}")

    closestDate
  }
}
