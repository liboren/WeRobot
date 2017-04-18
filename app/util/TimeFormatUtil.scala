package util

import java.util.Calendar
import java.util.Date
import com.twitter.util.{Time, TimeFormat}
import org.slf4j.LoggerFactory

/**
 * Created by liboren on 2017/04/18.
 */

object TimeFormatUtil{
  val log = LoggerFactory.getLogger(this.getClass)

  def toMilliseconds(date: String, format: String = "yyyy-MM-dd HH:mm:ss"): Long = {
    new TimeFormat(format).parse(date).inMilliseconds - 8 * 60 * 60 * 1000
  }

  def toLocalDate(date: Long, format: String = "yyyy-MM-dd HH:mm:ss"): String = {
    new TimeFormat(format).format(Time.fromMilliseconds(date + 8 * 60 * 60 * 1000))
  }

  def whichDayOfWeek:Int = { // 1-7 周日-周六
    val calendar = Calendar.getInstance()
    calendar.setTime(new Date())
    val week = calendar.get(Calendar.DAY_OF_WEEK)
    System.out.print("week:" + week)
    week
  }

  def howLongToNextHalfHour = {
    val calendar = Calendar.getInstance()
    calendar.setTime(new Date())
    val minute = calendar.get(Calendar.MINUTE)
    val restTime = if(minute >= 30){
      60 - minute
    }
    else{
      30 - minute
    }
    System.out.print("rest minutes:" + restTime)
    restTime
  }

  def formatThirtyMinute = { //0 ~ 47 表示00:00 ~ 23:30 每30分钟一个间隔
    val calendar = Calendar.getInstance()
    calendar.setTime(new Date())
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    System.out.println("hour:"+hour+"minute:"+minute)
    val minuteFormat = if(minute >= 30) 1 else 0
    hour * 2 + minuteFormat
  }

}
