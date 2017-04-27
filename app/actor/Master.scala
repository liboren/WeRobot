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

import scala.concurrent.Await
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
                       userCookieDao: UserCookieDao,
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

  var systemUuid = ""

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
        val slave = context.actorOf(Slave.props(userInfo, httpUtil, keywordResponseDao, memberDao,autoResponseDao, groupDao,userCookieDao), "slave" + userInfo.userid)
        context.watch(slave)
        slave ! BeginInit() // Todo
        self ! CreateSchedule(userInfo,slave)
      }
    case PushLogin(uin,cookie) => //向手机上推送网页版登录请求，需要uin和cookie，需之前登陆过才有cookie，此方式不用扫二维码（第一次登录时需要）
      val send = sender()
      val baseUrl = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxpushloginurl"
      val params = List(
        "uin" -> uin
      )

      httpUtil.getJsonRequestSend("push login", baseUrl,params,cookie).map { js =>
        try {
          val ret = (js \ "ret").as[String]
          val msg = (js \ "msg").as[String]
          if(ret.equals("0")){
            val uuid = (js \ "uuid").as[String]
            send ! Some(uuid)
//            self ! CheckUserLogin(uuid)
          }
          else{
            log.error("push login res error:"+msg)
          }
          //{"ret":"0","msg":"all ok","uuid":"Id8Q5Ypacg=="}
          //cookies ->
          /* pgv_pvi=2468073472; eas_sid=g1z4T9c2j0Y9o2C1k4o2n52114; RK=aWvrSIRKU2;webwxuvid=be846eda114e1e3a7d547f0a948520540447f8308237627c8d258b4ebfe2bcf2c7b7ce9f9c2b4949d3e1fba857bc0fda; tvfe_boss_uuid=6a10faebe6307698;o_cookie=195471917; pgv_pvid=6842915475; pgv_si=s3158088704; ptui_loginuin=195471917; ptisp=cm;ptcz=f7e1d84f0795b91eee3995230c46350467d084dfb7c3b51d4044ef016ed64235; pt2gguin=o0195471917; uin=o0195471917; qm_authimgs_id=0;
           qm_verifyimagesession=h01fd0c86c265a21ea671e80a6132bb2a03f5047ee53e6bbcdef64033dc87ccad9e7cd317a68a0bbdc4;
           douyu_loginKey=54a781806e3f818c77a90308daf7be41; refreshTimes=1;
           webwx_auth_ticket=CIsBEKCz/e0CGoABxtzfcWfe3v9Z+12ZEVInKbg80jyKQ9OJDf+nyHX4FXpDsulhfshrs1fLkavK6mjnf3NaQZ4+E+0s/QJWUZjg2dLwcujvVdQ34TcvEHprWAT8Co/n2rq7g+kKZTVPL8ohJBSV2m79xrOEgNZt9gDCMMsxAj2NDxw1SvoexLSfnnk=;
           login_frequency=2; last_wxuin=1082267300; wxloadtime=1493192558_expired; wxpluginkey=1493190927; wxuin=1082267300;
            mm_lang=zh_CN; MM_WX_NOTIFY_STATE=1; MM_WX_SOUND_STATE=1 */
          log.debug("push login res:"+js)
        }catch {
          case ex: Throwable =>
            log.error(s"error:" + js + s"ex: $ex")
        }
      }.onFailure {
        case e: Exception =>
          log.error("get 2d code uuid with EXCEPTION：" + e.getMessage)
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
//          self ! PushLogin("1082267300")
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
