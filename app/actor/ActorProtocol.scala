package actor

import akka.actor.ActorRef
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.collection.mutable
/**
  * Created by Macbook on 2017/4/13.
  */
 trait ActorProtocol

case class BaseInfo(nickName:String,displayName:String,headImgUrl:String,province:String,city:String,sex:Int)
import java.util.concurrent.ConcurrentHashMap
class UserInfo{
  var userid = 10000L
  var scan = ""
  var ticket = ""
  var uuid = ""
  var base_uri = "wx.qq.com/"
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
  var selfInfo:BaseInfo = null
  var ContactList = new ConcurrentHashMap[String,BaseInfo]() // 好友
  var GroupList= new ConcurrentHashMap[String,BaseInfo]() // 群
  var GroupMemeberList = new ConcurrentHashMap[String,ConcurrentHashMap[String,BaseInfo]]()  // 群友
  var PublicUsersList= new ConcurrentHashMap[String,BaseInfo]()  // 公众号／服务号
  var SpecialUsersList= new ConcurrentHashMap[String,BaseInfo]() // 特殊账号
  var autoReplyMode = false
  var syncHost = "webpush."
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

//Master
case class NewUserLogin(userInfo: UserInfo)
case class GetUuid()
case class CheckUserLogin(uuid:String)
case class CreateSchedule(userInfo: UserInfo,slave:ActorRef)
case class SlaveStop(userid:Long)
case class PushLogin(uin:String,cookie:String)
//Slave
case class BeginInit()
case class SendMessage(msg:String,from:String,to:String)
case class GetImg(imgUrl:String,path:String,name:String)
case class SendImgMessage(mediaid: String, from: String, to: String)
case class SendEmotionMessage(mediaid: String, from: String, to: String)
case class ProcessNewMessage(msgList:Seq[JsValue])
case class ReceivedNewMessage()
case class SyncCheck()
case class GetGroupContect(chatset:Array[String])
case class GetContect(seq:String)
case class StatusNotify()
case class WXInit()
case class GetTicketAndKey()
case class SyncCheckKey()
case class HandleMsg(fromUserName:String,toUserName:String,msgType:Int,msgid:String,msg:JsValue)
case class DeleteUserFromGroup(memNickName:String,groupNickName:String)
case class AddUserToGroup(memName:String,groupNickName:String)
case class InviteUserToGroup(userunionid:String,groupunionid:String)
case class SetGroupName(userunionid:String,name:String)
case class LogOut()
//ScheduleTask
case class ReceivedTask(userInfo: UserInfo,slave:ActorRef)