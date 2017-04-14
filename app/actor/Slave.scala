package actor


import javax.inject.Inject

import akka.actor.{Actor, Props}
import models.dao.KeywordResponseDao
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue, Json}
import util.{HttpUtil, ReplyUtil, SecureUtil}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import concurrent.duration._
/**
  * Created by Macbook on 2017/4/13.
  */

object Slave{

  def props(userInfo: UserInfo,httpUtil: HttpUtil,keywordResponseDao:KeywordResponseDao) = Props(new Slave(userInfo,httpUtil,keywordResponseDao))
}

class Slave @Inject() (userInfo: UserInfo,httpUtil: HttpUtil,keywordResponseDao:KeywordResponseDao)  extends Actor with ActorProtocol {

  private final val log = Logger(this.getClass)
  log.debug("------------------  Slave created")


  val groupMap = new scala.collection.mutable.HashMap[String, scala.collection.mutable.HashMap[String, String]]()
  val memberMap = new scala.collection.mutable.HashMap[String, String]()


  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    log.info(s"${self.path.name} starting...")
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.info(s"${self.path.name} stopping...")
  }

  def createUserNameData(seq: Array[String]): Array[JsObject] = {
    seq.map { m =>
      Json.obj(
        "UserName" -> m,
        "EncryChatRoomId" -> ""
      )
    }
  }

  def createSyncKey(Synckey: JsObject): String = {
    (Synckey \ "List").as[Seq[JsValue]].map { m =>
      val key = (m \ "Key").as[Int]
      val value = (m \ "Val").as[Long]
      key + "_" + value
    }.mkString("|")
  }

  override def receive: Receive = {
    case BeginInit() =>
      self ! GetTicketAndKey()
    case SendMessage(msg: String, from: String, to: String) => //回复消息
      val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg"
      val cookies = userInfo.cookie
      val curTime = System.currentTimeMillis()
      val LocalID = curTime + SecureUtil.nonceDigit(4)
      val params = List(
        "pass_ticket" -> userInfo.pass_ticket
      )
      val postData = Json.obj(
        "BaseRequest" -> Json.obj(
          "Uin" -> userInfo.wxuin,
          "Sid" -> userInfo.wxsid,
          "Skey" -> userInfo.skey,
          "DeviceID" -> userInfo.deviceId
        ),
        "Msg" -> Json.obj(
          "Type" -> "1",
          "Content" -> msg, //要发送的消息
          "FromUserName" -> from, //自己ID
          "ToUserName" -> to, //好友ID
          "LocalID" -> LocalID, //与ClientMsgId相同
          "ClientMsgId" -> LocalID //时间戳左移4位加4位随机数
        ),
        "Scene" -> "0"

      )

      httpUtil.postJsonRequestSend("sendMessage", baseUrl, params, postData, cookies).map { js =>
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
        } catch {
          case ex: Throwable =>
            log.error(s"error:" + js + s"ex: $ex")
        }
      }
    case ProcessNewMessage(msgList: Seq[JsValue]) => //处理收到的新消息
      if (msgList.nonEmpty) {
        val keywordList = Await.result(keywordResponseDao.getKeywordResponseList(userInfo.userid),10.second)
        msgList.foreach { msg =>
          val fromUserName = (msg \ "FromUserName").as[String]
          //@@0528655d6b576ac0ea5772ac3a41e42a1b4368aaad304c67b74f4c4696569d28
          val toUserName = (msg \ "ToUserName").as[String]
          val msgType = (msg \ "MsgType").as[Int]
          val content = (msg \ "Content").as[String]

          log.debug(s"收到消息(type:$msgType)内容：【$content 】来自【${fromUserName}】")
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
                    self ! SendMessage(response,userInfo.username, fromUserName)
                  }
                  else {
                    //                    val response = Await.result(chatApi.chatWithRobot(msg, ""), 27.seconds)
                    val response = null
                    if (response == null) {
                    }
                    else {
                      //                      Await.result(sendMessage(passTicket, uin, sid, skey, deviceId, username, formUserName, response, cookies), 27.seconds)
                      self ! SendMessage(response,userInfo.username, fromUserName)
                    }
                  }
                }
              }
              else {
                //是否有满足关键词回复

                val response = ReplyUtil.autoReply(content, keywordList)
                if (response != null) {
//                  log.debug("!!!!!!!!!!!!!!!!!!!!!!")
                  //                  Await.result(sendMessage(passTicket, uin, sid, skey, deviceId, username, formUserName, response, cookies), 27.seconds)
                  self ! SendMessage(response,userInfo.username, fromUserName)
                }
              }
            case 3 => // 图片消息
              //如果是图片消息，通过MsgId字段获取msgid，然后调用以下接口获取图片，type字段为空为大图，否则是缩略图
              //https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetmsgimg?MsgID=4880689959463718121&type=slave&skey=@crypt_f6c3cb1f_8b158d6e5d7df945580d590bd7612083
            case 34 => // 语音消息
            case 47 => // 动画表情
              // content字段里除了发送人id，还会有一个cdnurl字段，里面是动画表情的地址
              //Content":"@b1314d7ff68c30ceb8617667ca9eabfe:<br/>&lt;msg&gt;&lt;emoji fromusername = \"Suk_Ariel\" tousername = \"7458242548@chatroom\" type=\"2\" idbuffer=\"media:0_0\" md5=\"89fb4ee355c265bacee2766bec232a5e\" len = \"5372\" productid=\"\" androidmd5=\"89fb4ee355c265bacee2766bec232a5e\" androidlen=\"5372\" s60v3md5 = \"89fb4ee355c265bacee2766bec232a5e\" s60v3len=\"5372\" s60v5md5 = \"89fb4ee355c265bacee2766bec232a5e\" s60v5len=\"5372\" cdnurl = \"http://emoji.qpic.cn/wx_emoji/weXlICicxWBYKtZe4tu8YALX7CLJMXRiaV3J7vpBJ7KN2efLxbYjFkYw/\" designerid = \"\" thumburl = \"\" encrypturl = \"http://emoji.qpic.cn/wx_emoji/weXlICicxWBYKtZe4tu8YALX7CLJMXRiaVOZv37pyB6NIx3MNSzIqKvA/\" aeskey= \"07260f60e0d2c9f26a4ed3d5ea5ffd39\" width= \"48\" height= \"48\" &gt;&lt;/emoji&gt; &lt;/msg&gt;"
            case 48 => // 位置消息
            case 49 => // 分享链接
            //              val url = (msg \ "Url").as[String]
            //              if (url.startsWith("http://yys.163.com/h5/time")) {
            //                log.debug("receive URL:" + url)
            //                val sharePage = url.split("share_page=")(1).split("&")(0)
            //                val jieguo = shuapiao(sharePage)
            //                val total = jieguo._1
            //                val expire = jieguo._2
            //                val win = jieguo._3
            //
            //                var sendUserName = ""
            //                var realContent = ""
            //                var sendDisplayName = ""
            //                if(formUserName.startsWith("@@")) {//如果是群消息
            //                  sendUserName = content.split(":<br/>")(0)
            //                  sendDisplayName = groupMap.getOrElse(formUserName, new HashMap[String, String]).getOrElse(sendUserName, "")
            //                  realContent = content.split(":<br/>")(1)
            //                }
            //
            //                //@<span class=\"emoji emoji1f433\"></span> 樂瑥（海豚emoji表情）
            //                val str = s"@${sendDisplayName} 收到活动链接，一番努力后总共已经点亮了${total - expire}个爱心(新增${win}个)(上限10个)，快上游戏看积分有没增加吧~(账号总数:${total} 失效:${expire} 在线:${total - expire} 新增点亮:${win} 重复点亮:${total - expire - win})"
            //                Await.result(sendMessage(passTicket, uin, sid, skey, deviceId, username, formUserName, str, cookies), 27.seconds)
            //              }
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
    case ReceivedNewMessage() => // 获取新消息
      log.info("ReceivedNewMessage")
      val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsync"
      val cookies = userInfo.cookie
      val curTime = System.currentTimeMillis()
      val params = List(
        "sid" -> userInfo.wxsid,
        "skey" -> userInfo.skey,
        "pass_ticket" -> userInfo.pass_ticket,
        "lang" -> "zh_CN"
      )
      val postData = Json.obj(
        "BaseRequest" -> Json.obj(
          "Uin" -> userInfo.wxuin,
          "Sid" -> userInfo.wxsid,
          "Skey" -> userInfo.skey,
          "DeviceID" -> userInfo.deviceId
        ),
        "SyncKey" -> userInfo.SyncKey,
        "rr" -> (~curTime.toInt).toString
      )

      httpUtil.postJsonRequestSend("getMessage", baseUrl, params, postData, cookies).map { js =>
        try {
          log.debug("getMessage with return msg:" + js)
          val ret = (js \ "BaseResponse" \ "Ret").as[Int]
          if (ret == 0) {
            //返回成功
            val addMsgCount = (js \ "AddMsgCount").as[Int]
            val addMsgList = (js \ "AddMsgList").as[Seq[JsValue]]
            val Synckey = (js \ "SyncKey").as[JsObject]
            val synckey = createSyncKey(Synckey)
            userInfo.SyncKey = Synckey
            userInfo.synckey = synckey
            if(addMsgList.nonEmpty)
              self ! ProcessNewMessage(addMsgList)
            //            (synckey,addMsgList)
          }
          else {
            val errMsg = (js \ "BaseResponse" \ "ErrMsg").as[String]
            null
          }
          self ! SyncCheck()
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

        } catch {
          case ex: Throwable =>
            log.error(s"error:" + js + s"ex: $ex")
            null
        }
      }
    case SyncCheckKey() => // 当synccheck返回的selector是4或6时，需要用syncCheckKey更新而不是syncKey
      log.info("SyncCheckKey")
      val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsync"
      val cookies = userInfo.cookie
      val curTime = System.currentTimeMillis()
      val params = List(
        "sid" -> userInfo.wxsid,
        "skey" -> userInfo.skey,
        "pass_ticket" -> userInfo.pass_ticket,
        "lang" -> "zh_CN"
      )
      val postData = Json.obj(
        "BaseRequest" -> Json.obj(
          "Uin" -> userInfo.wxuin,
          "Sid" -> userInfo.wxsid,
          "Skey" -> userInfo.skey,
          "DeviceID" -> userInfo.deviceId
        ),
        "SyncKey" -> userInfo.SyncKey,
        "rr" -> (~curTime.toInt).toString
      )

      httpUtil.postJsonRequestSend("getMessage", baseUrl, params, postData, cookies).map { js =>
        try {
          log.debug("getMessage with return msg:" + js)
          val ret = (js \ "BaseResponse" \ "Ret").as[Int]
          if (ret == 0) {
            //返回成功
            val addMsgCount = (js \ "AddMsgCount").as[Int]
            val addMsgList = (js \ "AddMsgList").as[Seq[JsValue]]
            val SyncCheckKey = (js \ "SyncCheckKey").as[JsObject]
            val synccheckkey = createSyncKey(SyncCheckKey)
            userInfo.SyncKey = SyncCheckKey
            userInfo.synckey = synccheckkey
            if(addMsgList.nonEmpty)
              self ! ProcessNewMessage(addMsgList)
          }
          else {
            val errMsg = (js \ "BaseResponse" \ "ErrMsg").as[String]
            null
          }
          self ! SyncCheck()
        } catch {
          case ex: Throwable =>
            log.error(s"error:" + js + s"ex: $ex")
            null
        }
      }
    case SyncCheck() => //心跳检查，是否有新消息
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

        userInfo.lastCheckTs = System.currentTimeMillis()
        val baseUrl = "http://" + host + "/cgi-bin/mmwebwx-bin/synccheck"
        val cookies = userInfo.cookie
        val curTime = System.currentTimeMillis().toString
        val params = List(
          "skey" -> userInfo.skey,
          "synckey" -> userInfo.synckey,
          "deviceid" -> userInfo.deviceId, //e346659865782051
          "uin" -> userInfo.wxuin,
          "sid" -> userInfo.wxsid,
          "r" -> curTime,
          "_" -> System.currentTimeMillis().toString
        )
        httpUtil.getBodyRequestSend("synccheck", baseUrl, params, cookies).map { body =>
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
            log.debug("parse:" + body.toString.split("=")(1))
            val retcode = body.split("=")(1).split("\"")(1)
            val selector = body.split("=")(1).split("\"")(3)
            if (retcode.equals("0")) {
              if (selector.equals("2")) { //收到新消息
                self ! ReceivedNewMessage()

              }
              else if(selector.equals("0")){
                Thread.sleep(1000)
                self ! SyncCheck()
              }
              else if(selector.equals("4") || selector.equals("6")){
                self ! SyncCheckKey()
                log.error("失去链接，原因retcode:" + retcode + " selector:" + selector)
//                self ! PoisonPill
              }
            }
          } catch {
            case ex: Throwable =>
              log.error(s"error:" + body + s"ex: $ex")
              false
          }
        }
//        if(System.currentTimeMillis() - userInfo.lastCheckTs < userInfo.TimeOut) {
//          Thread.sleep(System.currentTimeMillis() - userInfo.lastCheckTs)
//        }
    case GetGroupContect() => // 获取群组信息
      val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxbatchgetcontact"

      val cookies = userInfo.cookie
      val curTime = System.currentTimeMillis().toString
      val userList = createUserNameData(userInfo.chatset)
      val count = userList.length
      val params = List(
        "pass_ticket" -> userInfo.pass_ticket,
        "type" -> "ex",
        "r" -> curTime,
        "lang" -> "zh_CN"
      )
      val postData = Json.obj(
        "BaseRequest" -> Json.obj(
          "Uin" -> userInfo.wxuin,
          "Sid" -> userInfo.wxsid,
          "Skey" -> userInfo.skey,
          "DeviceID" -> userInfo.deviceId
        ),
        "Count" -> count,
        "List" -> userList
      )

      httpUtil.postJsonRequestSend("get group contact", baseUrl, params, postData, cookies).map { js =>
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
          val ret = (js \ "BaseResponse" \ "Ret").as[Int]
          if (ret == 0) {
            val contactList = (js \ "ContactList").as[Seq[JsValue]]
            log.info("get group contact length:" + contactList.length)
            contactList.foreach { m =>
              val nickname = (m \ "NickName").as[String]
              log.debug("nickname" + nickname)
              if (nickname.equals("盖世英雄")) {
                val groupUserName = (m \ "UserName").as[String]
                val memberList = (m \ "MemberList").as[Seq[JsValue]]
                memberList.foreach { member =>
                  val memUserName = (member \ "UserName").as[String]
                  val memNickName = (member \ "NickName").as[String]
                  //Todo 这里获取更多的用户信息
                  val memDisplayName = (member \ "DisplayName").as[String]
                  memberMap.put(memUserName, memDisplayName) // (用户id，用户在群里的昵称)
                }
                groupMap.put(groupUserName, memberMap) // (群id，用户map)
              }
            }

            self ! SyncCheck()
          }
          else {
            log.error(s"get group contact error:" + js)
          }
          log.info("get group contact result:" + js)
        } catch {
          case ex: Throwable =>
            log.error(s"error:" + js + s"ex: $ex")
            null
        }
      }
    case GetContect() => // 获取联系人信息
      log.info("start getContect")
      val curTime = System.currentTimeMillis().toString
      val baseUrl = s"http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact"

      val params = List(
        "lang" -> "zh_CN",
        "pass_ticket" -> userInfo.pass_ticket,
        "r" -> curTime,
        "seq" -> "0",
        "skey" -> userInfo.skey
      )

      httpUtil.getJsonRequestSend("getContect", baseUrl, params).map { js =>
        try {
          log.debug("获取联系人信息" + js.toString())
          //Todo 这里获取不到联系人信息

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

          self ! GetGroupContect()
          None
        } catch {
          case ex: Throwable =>
            log.error(s"error:" + js + s"ex: $ex")
            None
        }
      }
    case StatusNotify() => //异步通知
      log.info("webwxstatusnotify")
      val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxstatusnotify"

      val cookies = userInfo.cookie
      val curTime = System.currentTimeMillis().toString
      log.debug(s"current time:$curTime")
      val params = List(
        "pass_ticket" -> userInfo.pass_ticket,
        "lang" -> "zh_CN"
      )
      val postData = Json.obj(
        "BaseRequest" -> Json.obj(
          "Uin" -> userInfo.wxuin,
          "Sid" -> userInfo.wxsid,
          "Skey" -> userInfo.skey,
          "DeviceID" -> userInfo.deviceId //2-17位随机数字
        ),
        "Code" -> "3",
        "FromUserName" -> userInfo.username,
        "ToUserName" -> userInfo.username,
        "ClientMsgId" -> curTime
      )

      httpUtil.postJsonRequestSend("webwxstatusnotify", baseUrl, params, postData, cookies).map { js =>
        try {
          log.debug("webwxstatusnotify:" + js)
          self ! GetContect()
        } catch {
          case ex: Throwable =>
            log.error(s"error:" + js + s"ex: $ex")
            None
        }
      }
    case WXInit() => //初始化
      log.info("weixin init")
      val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit"

      val cookies = userInfo.cookie
      val curTime = System.currentTimeMillis()
      log.debug(s"current time:$curTime")

      val params = List(
        "pass_ticket" -> userInfo.pass_ticket,
        "r" -> (~curTime.toInt).toString,
        "lang" -> "zh_CN"
      )
      val postData = Json.obj(
        "BaseRequest" -> Json.obj(
          "Uin" -> userInfo.wxuin,
          "Sid" -> userInfo.wxsid,
          "Skey" -> userInfo.skey,
          "DeviceID" -> userInfo.deviceId //2-17位随机数字
        )
      )


      httpUtil.postJsonRequestSend("weixin init", baseUrl, params, postData, cookies).map { js =>
        try {
          log.debug("weixin init res:" + js)
          val ret = (js \ "BaseResponse" \ "Ret").as[Int]
          log.debug("ret:" + ret)
          if (ret == 0) {
            val count = (js \ "Count").as[Int]
            val username = (js \ "User" \ "UserName").as[String]
            val userList = js \ "ContactList" \\ "UserName"
            val chatSet = (js \ "ChatSet").as[String].split(",")
            val Synckey = (js \ "SyncKey").as[JsObject]
            var synckey = createSyncKey(Synckey)
            log.debug("chat set:" + chatSet)

            userInfo.username = username
            userInfo.SyncKey = Synckey
            userInfo.synckey = synckey
            userInfo.chatset = chatSet
            self ! StatusNotify()
            //            (username,chatSet,synckey)
          }
          else {
            val errMsg = (js \ "BaseResponse" \ "ErrMsg").as[String]
            log.error("weixin init error:" + errMsg)
            null
          }

        } catch {
          case ex: Throwable =>
            log.error(s"weixin init error:$js")
            log.error(s"ex: $ex")
            null
        }
      }
    case GetTicketAndKey() => // 获取ticket
      log.info("get Ticket And Key")
      val baseUrl = "http://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage"

      val curTime = System.currentTimeMillis().toString
      log.debug(s"current time:$curTime")
      val params = List(
        "ticket" -> userInfo.ticket,
        "uuid" -> userInfo.uuid,
        "lang" -> "zh_CN",
        "scan" -> userInfo.scan,
        "fun" -> "new",
        "version" -> "v2"
      )
      httpUtil.getXMLRequestSend("get Ticket And Key", baseUrl, params).map { res =>
        try {
          val xml = res._1
          val cookies = res._2
          val cookie = cookies.map(m => m.name.get + "=" + m.value.get).mkString(";")
          log.debug("get request cookies:" + cookie)
          val ret = xml \\ "error" \\ "ret" text
          val message = xml \\ "error" \\ "message" text


          if (ret == "0") {
            val skey = xml \\ "error" \\ "skey" text
            val wxsid = xml \\ "error" \\ "wxsid" text
            val wxuin = xml \\ "error" \\ "wxuin" text
            val pass_ticket = xml \\ "error" \\ "pass_ticket" text
            val isgrayscale = xml \\ "error" \\ "isgrayscale" text

            userInfo.skey = skey
            userInfo.wxsid = wxsid
            userInfo.wxuin = wxuin
            userInfo.pass_ticket = pass_ticket
            userInfo.cookie = cookie
            self ! WXInit()
          }
          else {
            log.error(s"get Ticket And Key error:$message")
            null
          }

        } catch {
          case ex: Throwable =>
            log.error(s"error:" + res._1 + s"ex: $ex")
            null
        }
      }

  }
}
