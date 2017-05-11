package controllers

import java.net.{URLDecoder, URLEncoder}

import actor.{CheckUserLogin, GetUuid, PushLogin}
import akka.actor.ActorRef
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import common.AppSettings
import models.JsonProtocols
import org.apache.xerces.impl.io.UTF8Reader
import play.api._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.WSCookie
import play.api.mvc._
import util.{HttpUtil, SecureUtil}
import util._
import akka.pattern.ask

import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.control.Breaks
import common.Constants
import common.Constants.WeixinAPI
import akka.util.Timeout
import models.dao.UserCookieDao

@Singleton
class Application @Inject()(
                                httpUtil: HttpUtil,
                                chatApi:chatApi,
                                actionUtils: ActionUtils,
                                appSettings: AppSettings,
                                userCookieDao: UserCookieDao,
                                @Named("configured-master") master: ActorRef
                           ) extends Controller with JsonProtocols{
  import actionUtils._

  import concurrent.duration._

  private val log = Logger(this.getClass)
  implicit val timeout = Timeout(30.seconds)

  val sysAdmin = actionUtils.LoggingAction andThen AdminAction
  System.setProperty("jsse.enableSNIExtension", "false")


  def index = LoggingAction.async { implicit request => //默认页面，跳转到登录页
    Future.successful(Ok(views.html.login("WeRobot",None)))

  }

  def scan(userid:String) = sysAdmin.async { implicit request => //扫码登录页面
    Future.successful(Ok(views.html.scanlogin("WeRobot",None)))
  }

  def homepage = sysAdmin.async { implicit request => //扫码登录页面
    Future.successful(Ok(views.html.homepage("WeRobot",None)))

  }
  def test = LoggingAction.async { implicit request =>
    while(true) {
      val baseUrl = "https://planetali.boomegg.cn/alipay/m/zhuanpan/play/"
      val curTime = System.currentTimeMillis().toString
      val postData = Map(
        "_mtkey" -> "11b8c2983119a74cf18f5cec83cf1ba8",
        "_skey" -> "4a4392bec1aaf6b720fc7bafeba4b695",
        "_uid" -> "56695500",
        "_device" -> "ios",
        "_channel" -> "ios",
        "_version" -> "2.8.6",
        "_lan" -> "zh_cn",
        "time" -> curTime,
        "build" -> "",
        "multiple" -> "1"
      )
      httpUtil.postFormRequestSend("test", baseUrl, List(), postData).map { res =>
        System.out.println(res)
      }
    }
    Future.successful(Ok("OK"))
  }

  def shuapiao(sharePage:String) = {
    val zhanghao = List(
      ("585247","4c843f9dc2cd11d9cba8b0055666c3709ceae2c0","22"),// 22
      ("1013332","a8f479ec8c4e192f6b6b38b2673efcfb30393306","李暴龙"),//李暴龙22流星抽抽折扇大弟弟阿宅徐尼玛花间SPLUS叉叉麋鹿qy
      ("1412644","5aab0939b7de94059169c79553ffef424e48d0a0","田宝宝"),//田宝宝
      ("314048","5678df5e6ba83fb1c5880b303f6054ea0572c686","流星"),//流星
      ("602636","18e0becc05255f34d2bd98ed6dbb6187d611df14","抽抽"),//抽抽
      ("392296","19c4a5af82080d72832fbe8472b5d3e4d3676847","折扇"),//折扇
      ("394118","f6ed88651eac0c2c145fa755ee726ff24823a62d","大弟弟"),//大弟弟
      ("306522","b62dda0c3a09a672f11c09593d684123140e7099","阿宅"),//阿宅
      ("1476313","672ce83167f89085153392b7e569889824821000","徐尼玛"),//徐尼玛-
      ("643759","e466747595703bd4a66e3993ad4a6a83d18f286e","花间"),//花间-
      ("1487257","e202483a20de25dea8792087e15fbf623ac1b871","小明"),//小明
      ("1271046","b4da64555bd1f4bc4ebff4421c59c34520f96e7f","SPLUS"),//SPLUS-
      ("433957","403cf07acb2a669c13ccaf203b6a27af59e8275a","叉叉"),//叉叉
      ("187439","28e7b061a57299507ae08ccbe293680a301688bd","麋鹿"),//麋鹿-
      ("501669","2869bc2f364af0296a1b2608bca028416e605c20","qy")//qy

    )
    var total = 15
    var win = 0
    var expire = 0
      zhanghao.foreach { zh =>
          val result = Await.result(yysdongzhi(zh._1, zh._2, zh._3, sharePage), 10 seconds)
          if (result.isDefined) {
            if (result.get) {
              win = win + 1
            }
          }
        else{
            expire = expire + 1;
          }
      }

    log.debug(s"总共尝试进行了$total 次点亮,最终点亮了$win 个图标！")
    (total,expire,win)
  }


  //阴阳师非酋逆袭活动
  def yysdongzhi(user_id:String,token:String,name:String,share_page:String):Future[Option[Boolean]] = {
    log.info("开始准备点亮了!")
    //http://g37-36577.webapp.163.com/challenge?share_page=39e706aba1739965d6fb4f6227bc9c11&user_id=1013332&token=a8f479ec8c4e192f6b6b38b2673efcfb30393306&_=1489726491183&callback=jsonp2
    val baseUrl = "http://g37-36577.webapp.163.com/challenge"

    val curTime = System.currentTimeMillis().toString

    val param = List(
      "callback" -> "jsonp2",
      "user_id" -> user_id,
      "token" -> token,
      "share_page" -> share_page,
      "_" -> curTime
    )
    httpUtil.getBodyRequestSend("yys activity!", baseUrl,param,null).map { body =>
      try {

        log.debug("yys result:"+body)
        val js = Json.parse(body.split("jsonp2\\(")(1).split("\\)")(0))
        log.debug("js:"+js)

        //js:{"msg":"请先登录","_error":true,"success":false}
        //js:{"have_helped":true,"success":true}
        val success = (js \ "success").as[Boolean]
        val haveHelped = (js \ "have_helped").as[Boolean]
        if(success){
          if(haveHelped){
            Some(false)//已经点亮过
          }
          else{
            log.debug(name+"点亮图标成功！")
            Some(true)//成功点亮
          }
        }
        else{
          val msg = (js \ "msg").as[String]
          log.debug(name+"点亮图标失败，因为msg:"+msg)
          None//还未登录
        }



      }catch {
        case ex: Throwable =>
          log.debug(name+"点亮图标失败，:"+ex)
          None
      }
    }

  }

  def getUuidTest = LoggingAction.async{ implicit request =>

    userCookieDao.getCookieByUserid(10000L).flatMap{ res =>
      val curTime = System.currentTimeMillis() // 单位：秒
      if(res.isDefined && (curTime - res.get.createtime) <6 * 60 * 60 * 1000){ // 6小时内使用直接登录，不需要扫码
        (master ? PushLogin(res.get.uin,res.get.cookie)).flatMap {
          case Some(uuid:String) => Future.successful(Ok(successResponse(Json.obj("uuid" -> Json.toJson(uuid)))))
          case None => //推送登录消息失败
              (master ? GetUuid()).map {
              case Some(uuid:String) => Ok(successResponse(Json.obj("uuid" -> Json.toJson(uuid))))
              case None => Ok(jsonResponse(201,"error"))
              case _ => Ok(jsonResponse(201,"error"))
            }
          case _ =>Future.successful(Ok(jsonResponse(201,"error")))
        }
      }
      else{
        (master ? GetUuid()).map {
          case Some(uuid:String) => Ok(successResponse(Json.obj("uuid" -> Json.toJson(uuid))))
          case None => Ok(jsonResponse(201,"error"))
          case _ => Ok(jsonResponse(201,"error"))
        }
      }
    }

  }

  def checkUserLoginTest(uuid:String) = LoggingAction.async { implicit request =>
    log.info("check if user login")
    (master ? CheckUserLogin(uuid)).map {
      case code:String => Ok(successResponse(Json.obj("result" -> Json.toJson(code))))
      case _ => Ok(jsonResponse(201,"error"))
    }
  }

  //获取二维码uuid
  def getUuid = LoggingAction.async { implicit request =>
    log.info("Strat get 2d code uuid!")
    val baseUrl = WeixinAPI.getUuid.baseUrl
    val appid = WeixinAPI.getUuid.appid
    val curTime = System.currentTimeMillis().toString

    val postData = Map(
      "appid" -> appid,
      "fun" -> "new",
      "lang" -> "zh_CN",
      "_" -> curTime
    )
    httpUtil.postFormRequestSend("get 2d code uuid", baseUrl,List(),postData).map { js =>
      try {
        val res = js.toString
        log.info(s"$res")
        val code = res.split(";")(0).split(" ")(2)
        val uuid = res.split(";")(1).split("\"")(1)
        log.info(s"code:$code  uuid:$uuid")
        Ok(successResponse(Json.obj("uuid" -> Json.toJson(uuid))))
      }catch {
        case ex: Throwable =>
          log.error(s"error:" + js + s"ex: $ex")
          Ok(jsonResponse(201,"error"))
      }
    }

  }

  //获取二维码
//  def get2Dcode = LoggingAction.async { implicit request =>
//    log.info("Strat get 2d code file!")
//    val uuid = "IdNk7mIPtg=="
//    val baseUrl = "http://login.weixin.qq.com/qrcode/" + uuid
//
//    httpUtil.getFileRequestSend("get 2d code file", baseUrl,List()).map { js =>
//      try {
//        val res = js.toString
//        Ok(success)
//      }catch {
//        case ex: Throwable =>
//          log.error(s"error:" + js + s"ex: $ex")
//          Ok(jsonResponse(201,"error"))
//      }
//    }
//
//  }

  //查询用户是否扫码登录
  //window.code=xxx
  //408 登陆超时
  //201 扫描成功
  //200 确认登录
  //当返回200时，还会有
  //window.redirect_uri="http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?ticket=xxx&uuid=xxx&lang=xxx&scan=xxx";
  //拿到ticket
  def checkUserLogin(uuid:String) = LoggingAction.async { implicit request =>
    log.info("check if user login")

    val baseUrl = "http://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login"

    val curTime = System.currentTimeMillis()
    log.debug(s"current time:$curTime")
    val params = List(
      "loginicon" -> "true",
      "tip" -> "0",
      "uuid" -> uuid,
      "r" -> (~curTime.toInt).toString,
      "_" -> curTime.toString
    )
    httpUtil.getBodyRequestSend("check if user login", baseUrl,params,null).map { js =>
      try {
        val res = js.toString
        val code = res.split(";")(0).split("=")(1)
        //登录成功
        if(code == "200"){
          val param = res.split(";")(1)
          val list = param.split("&")
          val ticket = list(0).split("\\?")(1).split("=")(1)
          val scan = list(3).split("=")(1)
          getTicketAndKey(ticket,uuid,scan).map{ tuple => //tuple -> (skey,wxsid,wxuin,pass_ticket)
            val skey = tuple._1
            val sid = tuple._2
            val uin = tuple._3
            val passTicket = tuple._4
            val deviceId = "e" + util.SecureUtil.nonceDigit(15)
            val cookies = tuple._5
            wxInit(uuid,passTicket,skey,uin,sid,deviceId,cookies).map{ result =>
              val username = result._1
              log.debug(s"username:$username")
              var Synckey = result._3
              var synckey = (Synckey \ "List").as[Seq[JsValue]].map{m =>
                val key = (m \ "Key").as[Int]
                val value = (m \ "Val").as[Long]
                key + "_" + value
              }.mkString("|")

              webwxstatusnotify(passTicket,skey,uin,sid,username,deviceId,cookies).map { res =>

                getContect(passTicket, skey).map(result => //Todo 这里获取不到信息
                  log.info(result.toString())
                )
                getGroupContect(passTicket, uin, sid, skey, deviceId, createData(result._2), result._2.length, cookies).map { groupMap =>
                  log.info("this is groupMap:" + groupMap.toString())
                if(groupMap != null) {
                  while (true) {
                    try {
                      Await.result(synccheck(skey, synckey, deviceId, uin, sid, cookies).map { isReceiveNewMsg =>

                        if (isReceiveNewMsg) {
                          Await.result(getMessage(sid, skey, passTicket, uin, deviceId, Synckey, cookies).map { r =>
                            Synckey = r._1
                            log.debug("Synckey:" + Synckey)
                            synckey = (Synckey \ "List").as[Seq[JsValue]].map { m =>
                              val key = (m \ "Key").as[Int]
                              val value = (m \ "Val").as[Long]
                              key + "_" + value
                            }.mkString("|")

                            val msgList = r._2
                            log.debug("msgList:" + msgList)
                            if (msgList.nonEmpty) {
                              val a = msgList.map { msg =>
                                val formUserName = (msg \ "FromUserName").as[String]//@@0528655d6b576ac0ea5772ac3a41e42a1b4368aaad304c67b74f4c4696569d28
                                val toUserName = (msg \ "ToUserName").as[String]
                                val msgType = (msg \ "MsgType").as[Int]
                                val content = (msg \ "Content").as[String]



                                log.debug(s"收到消息(type:$msgType)来自【${formUserName}】，内容：【$content 】")
                                //TODO 统计群成员的消息，记录活跃状态,content构成[@sjkahdjkajsdjksd:<br/>msg][用户id:<br/>消息]

                                msgType match {
                                  case 1 => // 文本消息
                                    if (content.contains("@李暴龙")) {
                                      //是否开启自动聊天
                                      val info = content.split("@李暴龙")

                                      if (info.length > 1) {
                                        val msg = info(1).replace(" ", "").trim() // 把@姓名 后面的特殊空格去掉并去掉首尾的空格
                                        if (msg == "") {
                                          val response = "[疑问]"
                                          Await.result(sendMessage(passTicket, uin, sid, skey, deviceId, username, formUserName, response, cookies), 27.seconds)
                                        }
                                        else {
                                          val response = Await.result(chatApi.chatWithRobot(msg, ""), 27.seconds)
                                          if (response == null) {
                                          }
                                          else {
                                            Await.result(sendMessage(passTicket, uin, sid, skey, deviceId, username, formUserName, response, cookies), 27.seconds)
                                          }
                                        }
                                      }
                                    }
                                    else {
                                      //是否有满足关键词回复
                                      val keywordList = List()
                                      val response = ReplyUtil.autoReply(content, keywordList)
                                      if (response != null) {
                                        Await.result(sendMessage(passTicket, uin, sid, skey, deviceId, username, formUserName, response, cookies), 27.seconds)
                                      }
                                    }
                                  case 3 => // 图片消息
                                  case 34 => // 语音消息
                                  case 47 => // 动画表情
                                  case 48 => // 位置消息
                                  case 49 => // 分享链接
                                    val url = (msg \ "Url").as[String]
                                    if (url.startsWith("http://yys.163.com/h5/time")) {
                                      log.debug("receive URL:" + url)
                                      val sharePage = url.split("share_page=")(1).split("&")(0)
                                      val jieguo = shuapiao(sharePage)
                                      val total = jieguo._1
                                      val expire = jieguo._2
                                      val win = jieguo._3

                                      var sendUserName = ""
                                      var realContent = ""
                                      var sendDisplayName = ""
                                      if(formUserName.startsWith("@@")) {//如果是群消息
                                        sendUserName = content.split(":<br/>")(0)
                                        sendDisplayName = groupMap.getOrElse(formUserName, new HashMap[String, String]).getOrElse(sendUserName, "")
                                        realContent = content.split(":<br/>")(1)
                                      }

                                      //@<span class=\"emoji emoji1f433\"></span> 樂瑥（海豚emoji表情）
                                      val str = s"@${sendDisplayName} 收到活动链接，一番努力后总共已经点亮了${total - expire}个爱心(新增${win}个)(上限10个)，快上游戏看积分有没增加吧~(账号总数:${total} 失效:${expire} 在线:${total - expire} 新增点亮:${win} 重复点亮:${total - expire - win})"
                                      Await.result(sendMessage(passTicket, uin, sid, skey, deviceId, username, formUserName, str, cookies), 27.second)
                                    }
                                  case 62 => // 小视频
                                  case 10000 => // 系统消息
                                    //TODO 新人邀请 "FromUserName":"@@131473cf33f36c70ea5a95ce6c359a9e35f32c0ffbddf2e59e242f6a823ff2fa","ToUserName":"@fb6dce95633e13ca08e966a6f9a34e3c","MsgType":10000,"Content":"\" <span class=\"emoji emoji1f338\"></span>卷卷卷<span class=\"emoji emoji1f338\"></span>\"邀请\"Hou$e\"加入了群聊
                                  case 10002 => // 撤回消息
                                  case _ => // 其他消息

                                }

                              }
                            }
                            else {
                              log.debug("没有新消息....")
                            }
                          }, 27.seconds)
                        }
                      }, 27.seconds)

                    } catch {
                      case ex: Throwable =>
                        log.error(s"error:" + js + s"ex: $ex")
                        Ok(jsonResponse(201, "error"))
                    }

                  }
                }
              }
              }
            }

          }

        }
        Ok(successResponse(Json.obj("result" -> Json.toJson(code))))



      }catch {
        case ex: Throwable =>
          log.error(s"error:" + js + s"ex: $ex")
          Ok(jsonResponse(201,"error"))
      }
    }

  }
  def createData(seq:Array[String]) ={
    seq.map{ m =>
      Json.obj(
        "UserName" -> m,
        "EncryChatRoomId" -> ""
      )
    }
  }


  //获取认证所需的pass_ticket和skey
  //return (skey,wxsid,wxuin,pass_ticket)
  def getTicketAndKey(ticket:String, uuid:String, scan:String) = {
    log.info("get Ticket And Key")
    val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage"

    val curTime = System.currentTimeMillis().toString
    log.debug(s"current time:$curTime")
    val params = List(
      "ticket" -> ticket,
      "uuid" -> uuid,
      "lang" -> "zh_CN",
      "scan" -> scan,
      "fun" -> "new",
      "version" -> "v2"
    )

    httpUtil.getXMLRequestSend("get Ticket And Key", baseUrl,params).map { res =>
      try {
        val xml = res._1
        val cookies = res._2
        val cookie = cookies.map( m => m.name.get+"="+m.value.get).mkString(";")
        log.debug("get request cookies:"+cookie)
        val ret = xml \\ "error" \\ "ret" text
        val message = xml \\ "error" \\ "message" text


        if(ret == "0"){
          val skey = xml \\ "error" \\ "skey" text
          val wxsid = xml \\ "error" \\ "wxsid" text
          val wxuin = xml \\ "error" \\ "wxuin" text
          val pass_ticket = xml \\ "error" \\ "pass_ticket" text
          val isgrayscale = xml \\ "error" \\ "isgrayscale" text

          (skey,wxsid,wxuin,pass_ticket,cookie)
        }
        else{
          log.error(s"get Ticket And Key error:$message")
          null
        }

      }catch {
        case ex: Throwable =>
          log.error(s"error:" + res._1 + s"ex: $ex")
          null
      }
    }

  }

  //微信初始化,获得syncKey
  def wxInit(uuid:String, passTicket:String, skey:String, Uin:String, Sid:String,deviceId:String,cookies:String) = {
    log.info("weixin init")
    val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit"

    val curTime = System.currentTimeMillis()
    log.debug(s"current time:$curTime")

    val params = List(
      "pass_ticket" -> passTicket,
      "r" -> (~curTime.toInt).toString,
      "lang" -> "zh_CN"
    )
    val postData = Json.obj(
      "BaseRequest" -> Json.obj(
      "Uin" -> Uin,
      "Sid" -> Sid,
      "Skey" -> skey,
      "DeviceID" -> deviceId//2-17位随机数字
      )
    )

    httpUtil.postJsonRequestSend("weixin init", baseUrl,params,postData,cookies).map { js =>
      try {
        log.debug("weixin init res:"+js)
        val ret = (js \ "BaseResponse" \ "Ret").as[Int]
        log.debug("ret:"+ret)
        if(ret == 0){
          val count = (js \ "Count").as[Int]
          val username = (js \ "User" \ "UserName").as[String]
          val userList = js \ "ContactList" \\ "UserName"
          val chatSet = (js \ "ChatSet").as[String].split(",")
          val synckey = (js \ "SyncKey").as[JsObject]
          log.debug("chat set:"+chatSet)

          (username,chatSet,synckey)
        }
        else{
          val errMsg = (js \ "BaseResponse" \ "ErrMsg").as[String]
          log.error("weixin init error:" + errMsg)
          null
        }

      }catch {
        case ex: Throwable =>
          log.error(s"weixin init error:$js")
          log.error(s"ex: $ex")
          null
      }
    }

  }

  //webwxstatusnotify
  def webwxstatusnotify(passTicket:String, skey:String, uin:String, sid:String,fromUserName:String,devideId:String,cookies:String) = {
    log.info("webwxstatusnotify")
    val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxstatusnotify"

    val curTime = System.currentTimeMillis().toString
    log.debug(s"current time:$curTime")
    val params = List(
      "pass_ticket" -> passTicket,
      "lang" -> "zh_CN"
    )
    val postData = Json.obj(
      "BaseRequest" -> Json.obj(
        "Uin" -> uin,
        "Sid" -> sid,
        "Skey" -> skey,
        "DeviceID" -> devideId//2-17位随机数字
      ),
      "Code" -> "3",
      "FromUserName" -> fromUserName,
      "ToUserName" -> fromUserName,
      "ClientMsgId" -> curTime
    )

    httpUtil.postJsonRequestSend("webwxstatusnotify", baseUrl,params,postData,cookies).map { js =>
      try {
        log.debug("webwxstatusnotify:"+js)
        Ok(successResponse(Json.obj("result" -> js)))
      }catch {
        case ex: Throwable =>
          log.error(s"error:" + js + s"ex: $ex")
          Ok(jsonResponse(201,"error"))
      }
    }

  }

  //获取联系人信息
  def getContect(passTicket:String,skey:String) = {


    log.info("start getContect")
    val curTime = System.currentTimeMillis().toString
    val baseUrl = s"http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact"

    val params = List(
      "lang" -> "zh_CN",
      "pass_ticket" -> passTicket,
      "r" -> curTime,
      "seq" -> "0",
      "skey" -> skey
    )

    httpUtil.getJsonRequestSend("getContect", baseUrl,params,null).map { js =>
      try {
        log.debug(js.toString())
        /*
        {
    "BaseResponse": {
        "Ret": 0,
        "ErrMsg": ""
    },
    "MemberCount": 334,
    "MemberList": [
        {
            "Uin": 0,
            "UserName": xxx,
            "NickName": "Urinx",
            "HeadImgUrl": xxx,
            "ContactFlag": 3,
            "MemberCount": 0,
            "MemberList": [],
            "RemarkName": "",
            "HideInputBarFlag": 0,
            "Sex": 0,
            "Signature": "你好，我们是地球三体组织。在这里，你将感受到不一样的思维模式，以及颠覆常规的世界观。而我们的目标，就是以三体人的智慧，引领人类未来科学技术500年。",
            "VerifyFlag": 8,
            "OwnerUin": 0,
            "PYInitial": "URINX",
            "PYQuanPin": "Urinx",
            "RemarkPYInitial": "",
            "RemarkPYQuanPin": "",
            "StarFriend": 0,
            "AppAccountFlag": 0,
            "Statues": 0,
            "AttrStatus": 0,
            "Province": "",
            "City": "",
            "Alias": "Urinxs",
            "SnsFlag": 0,
            "UniFriend": 0,
            "DisplayName": "",
            "ChatRoomId": 0,
            "KeyWord": "gh_",
            "EncryChatRoomId": ""
        },
        ...
    ],
    "Seq": 0
}
        */

        Ok(successResponse(Json.obj("result" -> js)))
      }catch {
        case ex: Throwable =>
          log.error(s"error:" + js + s"ex: $ex")
          Ok(jsonResponse(201,"error"))
      }
    }

  }
  //获取微信群信息
  def getGroupContect(passTicket:String,uin:String,sid:String,skey:String,deviceId:String,userList:Seq[JsObject],count:Int,cookies:String) = {


    val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxbatchgetcontact"

    val curTime = System.currentTimeMillis().toString
    val params = List(
      "pass_ticket" -> passTicket,
      "type" -> "ex",
      "r" -> curTime,
      "lang" -> "zh_CN"
    )
    val postData = Json.obj(
      "BaseRequest" -> Json.obj(
        "Uin" -> uin,
        "Sid" -> sid,
        "Skey" -> skey,
        "DeviceID" -> deviceId
      ),
      "Count" -> count,
      "List" -> userList
    )

    httpUtil.postJsonRequestSend("get group contact", baseUrl,params,postData,cookies).map { js =>
      try {
        /*
        {
     BaseRequest: { Uin: xxx, Sid: xxx, Skey: xxx, DeviceID: xxx },
     Count: 群数量,
     List: [
         { UserName: 群ID, EncryChatRoomId: "" },
         ...
     ],
}
        */
        val groupMap = new scala.collection.mutable.HashMap[String,scala.collection.mutable.HashMap[String,String]]()
        val memberMap = new scala.collection.mutable.HashMap[String,String]()
        val ret = (js \ "BaseResponse" \ "Ret").as[Int]
        if(ret == 0){
          val contactList = (js \ "ContactList").as[Seq[JsValue]]
          log.info("get group contact length:"+contactList.length)
          contactList.foreach{m =>
            val nickname = (m \ "NickName").as[String]
            log.debug("nickname" + nickname)
            if(nickname.equals("盖世英雄")){
              val groupUserName = (m \ "UserName").as[String]
              val memberList = (m \ "MemberList").as[Seq[JsValue]]
              memberList.foreach{ member =>
                val memUserName = (member \ "UserName").as[String]
                val memNickName = (member \ "NickName").as[String]
                val memDisplayName = (member \ "DisplayName").as[String]
                memberMap.put(memUserName,memDisplayName)
              }
              groupMap.put(groupUserName,memberMap)
            }
          }

        }
        else{
          log.error(s"get group contact error:" + js)
        }

        log.info("get group contact result:"+js)
        groupMap
      }catch {
        case ex: Throwable =>
          log.error(s"error:" + js + s"ex: $ex")
          null
      }
    }

  }

  //同步刷新
  def synccheck(skey:String,synckey:String,deviceId:String,uin:String,sid:String,cookies:String) = {

    //http://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck?
    //(skey,@crypt_f6c3cb1f_b1d6c401c2516f32953bc47f2764d3aa),
    // (synckey,1_651360879|2_651360964|3_651360921|11_651358976|13_651358751|201_1482761524|1000_1482746582|1001_1482746612),
    // (deviceid,e888644980381257),
    // (uin,1082267300),
    // (sid,rmXG6Is+Kqs0WUuW),
    // (r,1482761545850),
    // (_,1482761545850))

    // r=1482759729531&
    // skey=@crypt_f6c3cb1f_6798817022cf9255d424988286d720b1&
    // sid=vTHGiO3r9c41kLR0&
    // uin=1082267300&
    // deviceid=e127756467164271&
    // synckey=1_651360879|2_651360920|3_651360842|11_651358976|13_651358751|201_1482759700|1000_1482746582|1001_1482746612&
    // _=1482759482003
    val host = "webpush.wx.qq.com"
    /*其中一个
    webpush.wx.qq.com
    webpush.weixin.qq.com
    webpush2.weixin.qq.com
    webpush.wechat.com
    webpush1.wechat.com
    webpush2.wechat.com
    webpush.wechatapp.com
    webpush1.wechatapp.com
     */

    val baseUrl = "http://"+host+"/cgi-bin/mmwebwx-bin/synccheck"

    val curTime = System.currentTimeMillis().toString
    val params = List(
      "skey" -> skey,
      "synckey" -> synckey,
      "deviceid" -> deviceId,//e346659865782051
      "uin" -> uin,
      "sid" -> sid,
      "r" -> curTime,
      "_" -> System.currentTimeMillis().toString
    )

    httpUtil.getBodyRequestSend("synccheck", baseUrl,params,cookies).map { body =>
      try {
        /*返回数据(String):
        window.synccheck={retcode:"xxx",selector:"xxx"}

        retcode:
            0 正常
            1100 失败/登出微信
        selector:
            0 正常
            2 新的消息
            7 进入/离开聊天界面
        */

        log.debug("parse:"+body.toString.split("=")(1))
        val retcode = body.split("=")(1).split("\"")(1)
        val selector = body.split("=")(1).split("\"")(3)

        if(retcode == "0" && selector == "2"){
          true
        }
        else{
          false
        }
      }catch {
        case ex: Throwable =>
          log.error(s"error:" + body + s"ex: $ex")
          false
      }
    }

  }

  //获取新消息
  def getMessage(sid:String,skey:String,passTicket:String,uin:String,deviceId:String,synckey:JsObject,cookies:String) = {


    val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsync"

    val curTime = System.currentTimeMillis()
    val params = List(
      "sid" -> sid,
      "skey" -> skey,
      "pass_ticket" -> passTicket,
      "lang" -> "zh_CN"
    )
    val postData = Json.obj(
      "BaseRequest" -> Json.obj(
        "Uin" -> uin,
        "Sid" -> sid,
        "Skey" -> skey,
        "DeviceID" -> deviceId
      ),
      "SyncKey" -> synckey,
      "rr" -> ( ~curTime.toInt).toString
    )

    httpUtil.postJsonRequestSend("getMessage", baseUrl,params,postData,cookies).map { js =>
      try {
        log.debug("getMessage with return msg:"+js)
        val ret = (js \ "BaseResponse" \ "Ret").as[Int]
        if(ret == 0){//返回成功
          val addMsgCount = (js \ "AddMsgCount").as[Int]
            val addMsgList = (js \ "AddMsgList").as[Seq[JsValue]]
            val synckey = (js \ "SyncKey").as[JsObject]
            (synckey,addMsgList)
        }
        else{
          val errMsg = (js \ "BaseResponse" \ "ErrMsg").as[String]
          null
        }
        /*返回数据(JSON):
        {
    'BaseResponse': {'ErrMsg': '', 'Ret': 0},
    'SyncKey': {
        'Count': 7,
        'List': [
            {'Val': 636214192, 'Key': 1},
            ...
        ]
    },
    'ContinueFlag': 0,
    'AddMsgCount': 1,
    'AddMsgList': [
        {
            'FromUserName': '',
            'PlayLength': 0,
            'RecommendInfo': {...},
            'Content': "",
            'StatusNotifyUserName': '',
            'StatusNotifyCode': 5,
            'Status': 3,
            'VoiceLength': 0,
            'ToUserName': '',
            'ForwardFlag': 0,
            'AppMsgType': 0,
            'AppInfo': {'Type': 0, 'AppID': ''},
            'Url': '',
            'ImgStatus': 1,
            'MsgType': 51,
            'ImgHeight': 0,
            'MediaId': '',
            'FileName': '',
            'FileSize': '',
            ...
        },
        ...
    ],
    'ModChatRoomMemberCount': 0,
    'ModContactList': [],
    'DelContactList': [],
    'ModChatRoomMemberList': [],
    'DelContactCount': 0,
    ...
}
        */

      }catch {
        case ex: Throwable =>
          log.error(s"error:" + js + s"ex: $ex")
          null
      }
    }

  }

  //发送消息
  def sendMessage(passTicket:String,uin:String,sid:String,skey:String,deviceId:String,from:String,to:String,msg:String,cookies:String) = {


    val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg"

    val curTime = System.currentTimeMillis()
    val LocalID = curTime + SecureUtil.nonceDigit(4)
    val params = List(
      "pass_ticket" -> passTicket
    )
    val postData = Json.obj(
      "BaseRequest" -> Json.obj(
        "Uin" -> uin,
        "Sid" -> sid,
        "Skey" -> skey,
        "DeviceID" -> deviceId
      ),
      "Msg" -> Json.obj(
        "Type" -> "1",
        "Content" -> msg,//要发送的消息
        "FromUserName" -> from,//自己ID
        "ToUserName" -> to,//好友ID
        "LocalID" -> LocalID,//与ClientMsgId相同
        "ClientMsgId" -> LocalID//时间戳左移4位加4位随机数
      ),
      "Scene" -> "0"

    )

    httpUtil.postJsonRequestSend("sendMessage", baseUrl,params,postData,cookies).map { js =>
      try {
        /*返回数据(JSON):
{
    "BaseResponse": {
        "Ret": 0,
        "ErrMsg": ""
    },
    ...
}
        */

        Ok(successResponse(Json.obj("result" -> js)))
      }catch {
        case ex: Throwable =>
          log.error(s"error:" + js + s"ex: $ex")
          Ok(jsonResponse(201,"error"))
      }
    }

  }

}