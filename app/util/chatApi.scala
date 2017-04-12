package util

import com.google.inject.{Inject, Singleton}
import common.AppSettings
import controllers.ActionUtils
import models.JsonProtocols
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * User: liboren.
  * Date: 2017/1/11.
  * Time: 9:59.
  */
@Singleton
class chatApi @Inject()(
                             httpUtil: HttpUtil,
                             val actionUtils: ActionUtils,
                             appSettings: AppSettings
                           ) extends Controller with JsonProtocols {

  import actionUtils._

  import concurrent.duration._

  private val log = Logger(this.getClass)




  def chatWithRobot(info:String,userid:String) = {

    log.info("chatWithRobot")
    val curTime = System.currentTimeMillis().toString
    val baseUrl = s"http://op.juhe.cn/robot/index"

    /*数据类型
    * "result":
        {
            "100000":"文本类数据",
            "200000":"网址类数据",
            "301000":"小说",
            "302000":"新闻",
            "304000":"应用、软件、下载",
            "305000":"列车",
            "306000":"航班",
            "307000":"团购",
            "308000":"优惠",
            "309000":"酒店",
            "310000":"彩票",
            "311000":"价格",
            "312000":"餐厅",
            "40002":"请求内容为空",
            "40005":"暂不支持该功能",
            "40006":"服务器升级中",
            "40007":"服务器数据格式异常",
            "50000":"机器人设定的“学用户说话”或者“默认回答”"
        },
    * */
    val params = List(
      "info" -> info,
      "dtype" -> "json",
      "loc" -> "",
      "lat" -> "",
      "userid" -> userid, // userid一致可以保持上下文一致
      "key" -> "0f9e5b979ff05e0e7ee451712854dcf6" //申请到的聚合数据接口专用的APPKEY
    )

    httpUtil.getJsonRequestSend("chatWithRobot", baseUrl,params).map { js =>
      try {
        val errCode = (js \ "error_code").as[Int]
        if(errCode == 0){

          val code = (js \ "result" \ "code").as[Int]//数据类型
          val text = (js \ "result" \ "text").as[String]
          log.info(s"chatWithRobot send:$info response:$text")
          text
        }
        else{
          val reason = (js \ "reason").as[String]
          log.error(s"chatWithRobot return error:$errCode reason:$reason")
          null
        }

      }catch {
        case ex: Throwable =>
          log.error(s"error:" + js + s"ex: $ex")
          null
      }
    }
  }
}
