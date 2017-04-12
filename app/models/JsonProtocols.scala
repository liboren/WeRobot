package models

import play.api.libs.json._
import play.api.mvc.Results




trait JsonProtocols {

  import play.api.libs.functional.syntax._


  def jsonResponse(errorCode: Int, msg: String) = {
    Json.obj("errCode" -> errorCode, "msg" -> msg)
  }

  def jsonResponse(errorCode: Int, msg: String, data: JsObject) = {
    Json.obj("errCode" -> errorCode, "msg" -> msg) ++ data
  }

  def successResponse(data: JsObject) = success ++ data

  val success = jsonResponse(0, "OK")


}