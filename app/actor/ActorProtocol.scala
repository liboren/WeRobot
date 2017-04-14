package actor

import play.api.libs.json.{JsObject, JsValue, Json}
/**
  * Created by Macbook on 2017/4/13.
  */
 trait ActorProtocol

class UserInfo{
  var userid = 10000L
  var scan = ""
  var ticket = ""
  var uuid = ""
  var base_uri = ""
  var redirect_uri = ""
  var wxuin = ""
  var wxsid = ""
  var skey = ""
  var pass_ticket = ""
  var deviceId = "e" + util.SecureUtil.nonceDigit(15)
  var BaseRequest = ""
  var synckey = ""
  var SyncKey:JsObject = null
  var username = ""
  var chatset:Array[String] = null
  var MemberList = ""
  var ContactList = ""  // 好友
  var GroupList = ""  // 群
  var GroupMemeberList = ""  // 群友
  var PublicUsersList = ""  // 公众号／服务号
  var SpecialUsersList = ""  // 特殊账号
  var autoReplyMode = false
  var syncHost = ""
  var user_agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36"
  var interactive = false
  var autoOpen = false
  var saveFolder = ""
  var saveSubFolders = Map("webwxgeticon"-> "icons", "webwxgetheadimg"-> "headimgs", "webwxgetmsgimg"-> "msgimgs",
    "webwxgetvideo"-> "videos", "webwxgetvoice"-> "voices", "_showQRCodeImg"-> "qrcodes")
  var appid = "wx782c26e4c19acffb"
  var lang = "zh_CN"
  var lastCheckTs : Long = System.currentTimeMillis()
  var memberCount = 0
//  var SpecialUsers = ("newsapp", "fmessage", "filehelper", "weibo", "qqmail", "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend", "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp",
//  "voip", "blogappweixin", "weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts", "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm", "notification_messages")
  var SpecialUsers = ""
  var TimeOut:Long = 10 * 1000  // 同步最短时间间隔（单位:秒）
  var media_count = -1

  var cookie = ""
}

case class NewUserLogin(userInfo: UserInfo)
case class GetUuid()
case class CheckUserLogin(uuid:String)
case class BeginInit()
case class SendMessage(msg:String,from:String,to:String)
case class ProcessNewMessage(msgList:Seq[JsValue])
case class ReceivedNewMessage()
case class SyncCheck()
case class GetGroupContect()
case class GetContect()
case class StatusNotify()
case class WXInit()
case class GetTicketAndKey()
case class SyncCheckKey()