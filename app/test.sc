import play.api.libs.json.Json
val body = "jsonp2({\"msg\": \"\\u5df2\\u7ecf\\u4e3a\\u4ed6\\u70b9\\u4eae\\u8fc7\\u4e86\", \"success\": false})"
val js = Json.parse(body.split("jsonp2\\(")(1).split("\\)")(0))
val msg =  new String((js \ "msg").as[String].getBytes("UTF-8"),"utf-8")