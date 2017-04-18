package actor

import javax.inject.{Inject, Singleton}

import akka.actor.{Actor, Cancellable, Props}
import common.Constants.WeixinAPI
import models.dao.{GroupDao, KeywordResponseDao, MemberDao, ScheduleResponseDao}
import play.api.Logger
import play.api.libs.json.Json
import util.HttpUtil

import concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Macbook on 2017/4/13.
  */

object ScheduleTask{

  def props(scheduleResponseDao: ScheduleResponseDao,groupDao: GroupDao) = Props(new ScheduleTask(scheduleResponseDao,groupDao))
}

@Singleton
class ScheduleTask @Inject()(scheduleResponseDao: ScheduleResponseDao,groupDao: GroupDao) extends Actor with ActorProtocol{
  private final val log = Logger(this.getClass)
  log.debug("------------------  Master created")

  @throws[Exception](classOf[Exception])
  override def preStart():Unit = {
    log.info(s"${self.path.name} starting...")
  }

  @throws[Exception](classOf[Exception])
  override def postStop():Unit = {
    log.info(s"${self.path.name} stopping...")
  }

  override def receive:Receive = {
    case ReceivedTask(userInfo,slave,triggerTime) =>
      log.debug("收到定时任务:"+triggerTime)
      scheduleResponseDao.getScheduleResponseByTriggerTime(triggerTime).map{ tasks =>
        tasks.foreach { task =>
          groupDao.getGroupByName(task.groupname,task.userid).map { groupOpt =>
            if(groupOpt.isDefined) {
              log.debug(s"触发定时任务：群（${task.groupname}）回复内容（${task.response}）")
              slave ! SendMessage(task.response, userInfo.username, groupOpt.get.groupunionid)
            }
          }
        }
      }


  }
}
