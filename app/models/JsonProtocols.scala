package models

import play.api.libs.json._
import play.api.mvc.Results
import models.tables.SlickTables._



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


  implicit val rKeywordresponseRow: Writes[rKeywordresponse] = new Writes[rKeywordresponse] {
    override def writes(o: rKeywordresponse): JsValue = {
      Json.obj(
        "id" -> o.id,
        "keyword" -> o.keyword,
        "restype" -> o.restype,
        "response" -> o.response,
        "triggertype" -> o.triggertype,
        "userid" -> o.userid
      )
    }
  }
}