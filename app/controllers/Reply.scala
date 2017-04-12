package controllers

import com.google.inject.{Inject, Singleton}
import common.AppSettings
import models.JsonProtocols
import play.api.Logger
import play.api.mvc.Controller
import util.{HttpUtil, chatApi}

/**
  * User: liboren.
  * Date: 2017/1/11.
  * Time: 15:52.
  */
@Singleton
class Reply @Inject()(
                             val actionUtils: ActionUtils,
                             appSettings: AppSettings
                           ) extends Controller with JsonProtocols {

  import actionUtils._

  import concurrent.duration._

  private val log = Logger(this.getClass)



  //新人入群欢迎

  //关键词自动回复
  def autoReply(content: String):String = {

    val keywords = List(
      ("1", "回复1", 1),
      ("2", "回复2", 0),
      ("3", "回复3", 1),
      ("4", "回复4", 0),
      ("5", "回复5", 0),
      ("6", "回复6", 1)
    )
    keywords.foreach { keyword =>
      keyword._3 match {
        case 1 => // 精确匹配
          if (keyword._1 == content) {
            return keyword._2
          }
        case 0 => // 模糊匹配
          if (content.contains(keyword._1)) {
            return keyword._2
          }
      }
    }

    null
  }

  //定时回复
}


