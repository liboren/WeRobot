package controllers

import com.google.inject.{Inject, Singleton}
import common.Constants
import common.Constants.SessionKey
import models.JsonProtocols
import models.dao.AdminDao
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by liuziwei on 2016/3/16.
 */
@Singleton
class Auth @Inject()(
                       val actionUtils: ActionUtils,
                       adminDao: AdminDao
                       ) extends Controller with JsonProtocols{

  val log = Logger(this.getClass)
  val loggingAuth = actionUtils.LoggingAction
  val sysAdmin = actionUtils.LoggingAction

  def loginForm = Form(
    tuple(
    "account" -> nonEmptyText,
    "password" -> nonEmptyText
    )
  )

  def loginSubmit = loggingAuth.async{implicit request =>
    loginForm.bindFromRequest.value match {
      case Some((account, password)) =>
        adminDao.getUserByAccount(account).map{ userInfo=>
          if(userInfo.isDefined && adminDao.checkPassword(userInfo.get, password)){
              val timestamp = System.currentTimeMillis().toString
              val userId = userInfo.get.userid
                    Ok(successResponse(Json.obj("userid"-> userInfo.get.userid.toString))).withSession(
                      SessionKey.userId -> userId.toString,
                      SessionKey.timestamp -> System.currentTimeMillis().toString
                    )
          }else{
            Ok(jsonResponse(10010, "user not exists or password error."))
          }
        }
//        if(account.equals("liboren") && password.equals("cxy920528")) {
//          Future.successful(Ok(successResponse(Json.obj("userid" -> "10000"))).withSession(
//            SessionKey.userId -> "10000",
//            SessionKey.timestamp -> System.currentTimeMillis().toString
//          ))
//        }
//        else{
//          Future.successful(Ok(jsonResponse(10010, "user not exists or password error.")))
//
//        }
      case None =>
        Future.successful(Ok(jsonResponse(10010, "Input format error.")))
    }
  }

}
