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

  def getKeywordResponseList(userid:Long) = LoggingAction.async{ request =>
    keywordResponseDao.getKeywordResponseList(userid).map{ keywordList =>
        Ok(successResponse(Json.obj("keywordList" -> Json.toJson(keywordList))))
    }
  }

  def createrKeywordResponse(keyword:String,restype:Int,response:String,triggertype:Int,userid:Long) = LoggingAction.async{ request =>
    keywordResponseDao.createrKeywordResponse(keyword,restype,response,triggertype,userid).map{ res =>
      if(res > 0){
        Ok(success)
      }
      else{
        Ok(jsonResponse(1001,"creater keyword response failed"))
      }
    }
  }

  def changeKeywordResponseList(id:Long,userid:Long,keyword:String,restype:Int,response:String,triggertype:Int) = LoggingAction.async{ request =>
    keywordResponseDao.changeKeywordResponseList(id,userid,keyword,restype,response,triggertype).map{ res =>
      if(res > 0){
        Ok(success)
      }
      else{
        Ok(jsonResponse(1002,"change keyword response failed"))
      }
    }
  }

  def deleteKeywordResponse(id:Long,userid:Long) = LoggingAction.async{ request =>
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
