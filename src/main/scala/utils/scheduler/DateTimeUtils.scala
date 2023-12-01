package utils.scheduler

import java.time.{Duration, LocalDateTime, ZoneId}
import java.util.Date

object DateTimeUtils {
  def dateToLocalDT(date: Date): LocalDateTime = {
    date.toInstant.atZone(ZoneId.systemDefault()).toLocalDateTime
  }

  def formattedDateTime(dt: LocalDateTime): String = {
    String.format(
      "%02d.%02d.%02d %d:%02d:%02d",
      dt.getDayOfMonth,
      dt.getMonthValue,
      dt.getYear,
      dt.getHour,
      dt.getMinute,
      dt.getSecond
    )
  }

  def formattedTimeLeft(start: LocalDateTime, end: LocalDateTime): String = {
    val diff = Duration.between(start, end)
    String.format("%dh %02dm %02ds", diff.toHours, diff.toMinutes % 60, diff.getSeconds % 60)
  }
}
