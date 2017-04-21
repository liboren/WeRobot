package actor

import javax.inject.{Inject, Singleton}

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, OneForOneStrategy, PoisonPill, Terminated}
import common.Constants.WeixinAPI
import models.dao._
import play.api.Logger
import play.api.libs.json.Json
import util.HttpUtil
import util.TimeFormatUtil._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Macbook on 2017/4/13.
  */



@Singleton
class Master @Inject()(httpUtil: HttpUtil,
                       keywordResponseDao: KeywordResponseDao,
                       memberDao: MemberDao,
                       groupDao: GroupDao,
                       autoResponseDao: AutoResponseDao,
                       scheduleResponseDao: ScheduleResponseDao) extends Actor with ActorProtocol{
  private final val log = Logger(this.getClass)
  log.debug("------------------  Master created")

//  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
//    case Terminated => Stop
//  }
  @throws[Exception](classOf[Exception])
  override def preStart():Unit = {
    log.info(s"${self.path.name} starting...")
  }

  @throws[Exception](classOf[Exception])
  override def postStop():Unit = {
    log.info(s"${self.path.name} stopping...")
  }

  override def receive:Receive = {
    case SlaveStop(userid) =>
      val slave = sender()
      log.info(s"收到slave(${slave.path.name})停止消息..向slave和schedule发送poison pill")
      context.unwatch(slave)
      val schedule = context.child("schedule"+userid).getOrElse(context.system.deadLetters)
      context.unwatch(schedule)
      slave ! PoisonPill
      schedule ! PoisonPill
    case CreateSchedule(userInfo,slave) =>
      val task = context.actorOf(ScheduleTask.props(scheduleResponseDao,groupDao),"schedule"+userInfo.userid)
      context.watch(task)
      context.system.scheduler.schedule(howLongToNextMinute.second,1.minute,task,ReceivedTask(userInfo,slave))
    case NewUserLogin(userInfo:UserInfo) => // 有新的用户扫码登录
      if(context.child("slave"+userInfo.userid).isDefined) {
        log.debug(s" slave(${userInfo.userid}) is existed.")
        context.child("slave"+userInfo.userid).get ! BeginInit()
      }
      else {
        val slave = context.actorOf(Slave.props(userInfo, httpUtil, keywordResponseDao, memberDao,autoResponseDao, groupDao), "slave" + userInfo.userid)
        context.watch(slave)
        slave ! BeginInit() // Todo
        self ! CreateSchedule(userInfo,slave)
      }
    case GetUuid() => // 获取二维码uuid
      val send = sender()
      log.info("Strat get 2d code uuid!")
      val baseUrl = WeixinAPI.getUuid.baseUrl
      val appid = WeixinAPI.getUuid.appid
      val curTime = System.currentTimeMillis().toString

      val params = List(
        "appid" -> appid,
        "fun" -> "new",
        "lang" -> "zh_CN",
        "_" -> curTime
      )
      httpUtil.getBodyRequestSend("get 2d code uuid", baseUrl,params,null).map { js =>
        try {
          val res = js.toString
          log.info(s"$res")
          val code = res.split(";")(0).split(" ")(2)
          val uuid = res.split(";")(1).split("\"")(1)
          log.info(s"code:$code  uuid:$uuid")
          send ! Some(uuid)
//          Ok(successResponse(Json.obj("uuid" -> Json.toJson(uuid))))
        }catch {
          case ex: Throwable =>
            log.error(s"error:" + js + s"ex: $ex")
            send ! None
//            Ok(jsonResponse(201,"error"))
        }
      }.onFailure {
        case e: Exception =>
          log.error("get 2d code uuid with EXCEPTION：" + e.getMessage)
      }
    case CheckUserLogin(uuid)  => // 检查用户是否已扫码并登录
      val send = sender()
      log.info("check if user login")

      val baseUrl = "http://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login"

      val curTime = System.currentTimeMillis()
      val params = List(
        "loginicon" -> "true",
        "tip" -> "0",
        "uuid" -> uuid,
        "r" -> (~curTime.toInt).toString,
        "_" -> curTime.toString
      )
      httpUtil.getBodyRequestSend("check if user login", baseUrl,params,null).map { js =>
        val res = js.toString
        log.debug("登入返回" + res)
        val code = res.split(";")(0).split("=")(1)
        //登录成功
        if (code == "200") {
          val param = res.split(";")(1)
          val list = param.split("&")
          val ticket = list(0).split("\\?")(1).split("=")(1)
          val scan = list(3).split("=")(1)
          val userInfo = new UserInfo()
          val baseUrl = param.split("https://")(1).split("cgi-bin")(0)
          userInfo.base_uri = baseUrl
          userInfo.uuid = uuid
          userInfo.ticket = ticket
          userInfo.scan = scan
          userInfo.userid = 10000L // Todo 这里修改当前用户id
          self ! NewUserLogin(userInfo)
        }
        send ! code
      }.onFailure {
        case e: Exception =>
          log.error("check if user login with EXCEPTION：" + e.getMessage)
      }
  }
}
