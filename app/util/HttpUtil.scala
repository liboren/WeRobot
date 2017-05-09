

package util


import java.io.{ByteArrayOutputStream, File, FileInputStream, FileOutputStream}
import java.security.Policy.Parameters
import java.util.concurrent.TimeoutException

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Source}
import akka.stream.{ActorMaterializer, IOResult, Materializer}
import akka.util.ByteString
import com.google.inject.{Inject, Singleton}
import org.asynchttpclient.{AsyncCompletionHandler, AsyncHttpClient, RequestBuilder, Response}
//import org.asynchttpclient.request.body.multipart.{ByteArrayPart, FilePart}
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.ws.ahc.AhcWSResponse
import play.api.libs.ws.{WS, WSClient, WSCookie}
import common.Constants.FilePath._
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.{DataPart, FilePart}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.concurrent.duration.Duration
import scala.concurrent.duration._

/**

  * User: Liboren's.

  * Date: 2016/3/4.

  * Time: 9:49.

  */
@Singleton
class HttpUtil @Inject()(
                          ws: WSClient) {

  private val log = Logger(this.getClass)
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

//  val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) "+
//  "AppleWebKit/537.36 (KHTML, like Gecko) "+
//  "Chrome/48.0.2564.109 Safari/537.36"
  val userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36"

  @throws(classOf[TimeoutException])
  def getJsonRequestSend(
                          methodName: String,
                          url: String,
                          parameters: List[(String, String)],
                          cookie:String) = {
    log.info("Get Request [" + methodName + "] Processing...")
//    log.debug(methodName + " url=" + url)
//    log.debug(methodName + " parameters=" + parameters)
    val futureResult = ws.
      url(url).
      withHeaders(("Cookie",cookie),("User-Agent",userAgent)).
      withFollowRedirects(follow = true).
      withRequestTimeout(Duration(10, scala.concurrent.duration.SECONDS)).
      withQueryString(parameters: _*).
      get().map { response =>
//      log.debug("getRequestSend response headers:" + response.allHeaders)
//      log.debug("getRequestSend response body:" + response.body)
//      log.debug("getRequestSend response cookies:" + response.cookies)
      if (response.status != 200) {
        val body = if (response.body.length > 1024) response.body.substring(0, 1024) else response.body
        val msg = s"getRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = ${body}"
        log.warn(msg)
      }

      response.json

    }

    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage, e)
        throw e
    }
    futureResult
  }

  @throws(classOf[TimeoutException])
  def getBodyRequestSend(
                          methodName: String,
                          url: String,
                          parameters: List[(String, String)],
                          cookies:String
                        )= {
//    log.info("Get Request [" + methodName + "] Processing...")
//    log.debug(methodName + " url=" + url)
//    log.debug(methodName + " parameters=" + parameters)
    val cookie = if(cookies == null) "" else cookies.toString
    val futureResult = ws.
      url(url).
      withHeaders(("Cookie",cookie),("User-Agent",userAgent)).
      withFollowRedirects(follow = true).
      withRequestTimeout(Duration(60, scala.concurrent.duration.SECONDS)).
      withQueryString(parameters: _*).
      get().map { response =>
//      log.debug("getRequestSend response headers:" + response.allHeaders)
//      log.debug("getRequestSend response body:" + response.body)
//      log.debug("getRequestSend response cookies:" + response.cookies)
      if (response.status != 200) {
        val body = if (response.body.length > 1024) response.body.substring(0, 1024) else response.body
        val msg = s"getRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = $body"
        log.warn(msg)
      }

      response.body

    }
    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage,e)
        throw e
    }
    futureResult
  }

  @throws(classOf[TimeoutException])
  def getXMLRequestSend(
                          methodName: String,
                          url: String,
                          parameters: List[(String, String)]) = {
//    log.info("Get Request [" + methodName + "] Processing...")
//    log.debug(methodName + " url=" + url)
//    log.debug(methodName + " parameters=" + parameters)
    val futureResult = ws.
      url(url).
      withHeaders(("User-Agent",userAgent)).
      withFollowRedirects(follow = true).
      withRequestTimeout(Duration(30, scala.concurrent.duration.SECONDS)).
      withQueryString(parameters: _*).
      get().map { response =>
//      log.debug("getRequestSend response headers:" + response.allHeaders)
//      log.debug("getRequestSend response body:" + response.body)
//      log.debug("getRequestSend response cookies:" + response.cookies)
      if (response.status != 200) {
        val body = if (response.body.length > 1024) response.body.substring(0, 1024) else response.body
        val msg = s"getRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = $body"
        log.warn(msg)
      }

      (response.xml,response.cookies)

    }
    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage, e)
        throw e
    }
    futureResult
  }

  @throws(classOf[TimeoutException])
  def postJsonRequestSend(
                           methodName: String,
                           url: String,
                           parameters: List[(String, String)],
                           postData: JsObject,
                           cookies:String
  ) = {
    log.info("Post Request [" + methodName + "] Processing...")
//    log.debug(methodName + " url=" + url)
//    log.debug(methodName + " parameters=" + parameters)
//    log.debug(methodName + " postData=" + postData.toString)
//    log.debug(methodName + " cookies=" + cookies)
    val futureResult = ws.
      url(url).
      withHeaders(("Cookie",cookies),("User-Agent",userAgent)).
      withFollowRedirects(follow = true).
      withRequestTimeout(Duration(20, scala.concurrent.duration.SECONDS)).
      withQueryString(parameters: _*).
      post(postData).map { response =>
//      log.debug("postJsonRequestSend response headers:" + response.allHeaders)
//      log.debug("postJsonRequestSend response body:" + response.body)
//      log.debug("postJsonRequestSend response cookies:" + response.cookies)
      if (response.status != 200) {
        val body = response.body.slice(0, 1024)
        val msg = s"postJsonRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = $body"
        log.warn(msg)
      }
      response.json
    }
    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage, e)
        throw e
    }
    futureResult
  }


  //上传文件
  def postFile(methodName:String,url:String,filePart:List[MultipartFormData.Part[Source[ByteString, Future[IOResult]]]]) = {

    val futureResult = ws.url(url).
      withHeaders(("Content-Type","multipart/form-data"),("User-Agent",userAgent)).
      post(Source(filePart)).map{ response =>
      if (response.status != 200) {
        val body = response.body.slice(0, 1024)
        val msg = s"postJsonRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = $body"
        log.warn(msg)
      }
      response.json
    }
    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage, e)
        throw e
    }
    futureResult
  }


  @throws(classOf[TimeoutException])
  def getFileRequestSend(
                          methodName: String,
                          url: String,
                          cookies:String,
                          parameters: List[(String, String)],name:String = null,path:String,fileType:String) = {
    log.info("Get Request [" + methodName + "] Processing...")
    log.debug(methodName + " url=" + url)
    log.debug(methodName + " parameters=" + parameters)
    val futureResult = ws.
      url(url).
      withHeaders(
        ("Cookie",cookies),
        ("User-Agent",userAgent),
        ("Range","bytes=0-"), //这里不加Range的话获取到的视频内容大小为0
        ("Accept","*/*")
      ).
      withFollowRedirects(follow = true).
      withRequestTimeout(Duration(10, scala.concurrent.duration.SECONDS)).
      withQueryString(parameters: _*).
      stream()
    val cur = System.currentTimeMillis()

    futureResult.flatMap{ a=>
      val headers = a.headers.headers
      val status = a.headers.status
      log.debug("status:"+status)
      log.debug("headers:"+headers)
//      log.debug("file body:"+a.body)
//      val fileType =
//        if(typeOpt.isDefined)
//          try {
//            typeOpt.get.head.split("/")(1)
//          }catch {
//            case e:Exception => "jpeg"
//          }
//        else
//          "jpeg"
      val fileName = if(name == null)cur.toString + "." + fileType else name + "." + fileType
      val dirPath = path
      val dir = new File(dirPath)
      if(!dir.exists()) dir.mkdir()
      val outputStream = new FileOutputStream(dirPath+fileName)
      //        val res = a.body.toMat(FileIO.toFile(outputStream))(Keep.right).run()
      //        res.map{ioRes =>
      //          if(ioRes.wasSuccessful){
      //            Some("../img/"+fileName)
      //          }else{
      //            ioRes.getError
      //            log.error(s"[$methodName] write to file failed")
      //            None
      //          }
      //        }
      a.body.runFold(new ArrayBuffer[Byte]()){(array,chunk) =>
        val bytes = chunk.toArray
        array.++=(bytes)
        array
      }.map{array =>
        try{
          outputStream.write(array.toArray)
          outputStream.close()
          Some(dirPath+fileName)
        }catch{
          case e:Exception =>
            log.error(s"[$methodName] write to file with exception",e)
            None
        }

      }
    }
  }


  //body为multipartformdata类型
  @throws(classOf[TimeoutException])
  def postFormRequestSend(
                           methodName: String,
                           url: String,
                           parameters: List[(String, String)],
                           postData:Map[String,String]) = {
//    log.info("Get Request [" + methodName + "] Processing...")
//    log.debug(methodName + " url=" + url)
    val postStr = postData.map(i => i._1 + "=" + i._2).mkString("&")
//    log.info(methodName + " postData=" + postStr)
    val futureResult = ws.
      url(url).
      withHeaders(("User-Agent",userAgent)).
      withFollowRedirects(follow = true).
      withRequestTimeout(Duration(10, scala.concurrent.duration.SECONDS)).
      withQueryString(parameters: _*).
      post(postStr).map { response =>
//      log.debug("getRequestSend response headers:" + response.allHeaders)
//      log.debug("getRequestSend response body:" + response.body)
//      log.debug("postFormRequestSend response cookies:" + response.cookies)
      if (response.status != 200) {
        val body = if (response.body.length > 1024) response.body.substring(0, 1024) else response.body
        val msg = s"getRequestSend http failed url = $url, status = ${response.status}, text = ${response.statusText}, body = $body"
        log.warn(msg)

      }
      response.body //TODO

    }
    futureResult.onFailure {
      case e: Exception =>
        log.error(methodName + " error:" + e.getMessage, e)
        throw e
    }
    futureResult

  }

}

