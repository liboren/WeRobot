package controllers

import play.api.Logger
import play.api.mvc.{Controller, WebSocket}
import models.dao.KeywordResponseDao
import com.google.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import models.JsonProtocols

/**
  * Created by liboren on 20167/4/22.
  */
@Singleton
class KeywordResponse @Inject()(
                       keywordResponseDao: KeywordResponseDao,
                       actionUtils: ActionUtils
                     ) extends Controller with JsonProtocols{
  import actionUtils._
  import concurrent.duration._

  private val log = Logger(this.getClass)

  def getKeywordResponseList = LoggingAction.async{ request =>
    val jsonData = request.body.asJson.get
    val userid = (jsonData \ "userid").as[Long]
    val groupnickname = (jsonData \ "groupnickname").as[String]
    keywordResponseDao.getKeywordResponseList(userid,groupnickname).map{ keywordList =>
        Ok(successResponse(Json.obj("keywordList" -> Json.toJson(keywordList))))
    }
  }

  def createrKeywordResponse = LoggingAction.async{ request =>
    val jsonData = request.body.asJson.get
    val keyword = (jsonData \ "keyword").as[String]
    val restype = (jsonData \ "restype").as[Int]
    val response = (jsonData \ "response").as[String]
    val triggertype = (jsonData \ "triggertype").as[Int]
    val userid = (jsonData \ "userid").as[Long]
    val groupnickname = (jsonData \ "groupnickname").as[String]
    val state = (jsonData \ "state").as[Int]
    keywordResponseDao.createrKeywordResponse(keyword,restype,response,triggertype,userid,groupnickname,state).map{ res =>
      if(res > 0){
        Ok(success)
      }
      else{
        Ok(jsonResponse(1001,"creater keyword response failed"))
      }
    }
  }

  def changeKeywordResponseList = LoggingAction.async{ request =>
    val jsonData = request.body.asJson.get
    val id = (jsonData \ "id").as[Long]
    val keyword = (jsonData \ "keyword").as[String]
    val restype = (jsonData \ "restype").as[Int]
    val response = (jsonData \ "response").as[String]
    val triggertype = (jsonData \ "triggertype").as[Int]
    val userid = (jsonData \ "userid").as[Long]
    val groupnickname = (jsonData \ "groupnickname").as[String]
    val state = (jsonData \ "state").as[Int]
    keywordResponseDao.changeKeywordResponseList(id,userid,keyword,restype,response,triggertype,groupnickname,state).map{ res =>
      if(res > 0){
        Ok(success)
      }
      else{
        Ok(jsonResponse(1002,"change keyword response failed"))
      }
    }
  }

  def deleteKeywordResponse = LoggingAction.async{ request =>
    val jsonData = request.body.asJson.get
    val id = (jsonData \ "id").as[Long]
    val userid = (jsonData \ "userid").as[Long]
    keywordResponseDao.deleteKeywordResponse(id,userid).map{ res =>
      if(res > 0){
        Ok(success)
      }
      else{
        Ok(jsonResponse(1003,"delete keyword response failed"))
      }
    }
  }


}
