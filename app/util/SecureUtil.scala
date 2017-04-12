package util

import java.net.{URLDecoder, URLEncoder}

import org.apache.commons.codec.digest.DigestUtils

import scala.util.Random

/**
 * User: Taoz
 * Date: 7/8/2015
 * Time: 8:42 PM
 */
object SecureUtil {

  val random = new Random(System.currentTimeMillis())

  val digits = Array(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
  )

  val chars = Array(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
  )


  def getSecurePassword(password: String, ip: String, timestamp: Long): String = {
    DigestUtils.md5Hex(DigestUtils.md5Hex(timestamp + password) + ip + timestamp)
  }

  def checkSignature(parameters: List[String], signature: String, secureKey: String) = {
    generateSignature(parameters, secureKey) == signature
  }

  def generateSignature(parameters: List[String], secureKey: String) = {
    val strSeq = ( secureKey :: parameters ).sorted.mkString("")
    //println(s"strSeq: $strSeq")
    DigestUtils.sha1Hex(strSeq)
  }

  def generateSignatureParameters(parameters: List[String], secureKey: String) = {
    val timestamp = System.currentTimeMillis().toString
    val nonce = nonceStr(6)
    val pList = nonce :: timestamp :: parameters
    val signature = generateSignature(pList, secureKey)
    (timestamp, nonce, signature)
  }

  def nonceStr(length: Int) = {
    val range = chars.length
    (0 until length).map { _ =>
      chars(random.nextInt(range))
    }.mkString("")
  }

  def nonceDigit(length: Int) = {
    val range = digits.length
    (0 until length).map { _ =>
      digits(random.nextInt(range))
    }.mkString("")
  }


  def checkStringSign(str: String, sign: String, secureKey: String) = {
    stringSign(str, secureKey) == sign
  }

  def stringSign(str: String, secureKey: String) = {
    DigestUtils.sha1Hex(secureKey + str)
  }


  def main(args: Array[String]) {

    val pList = List(
      "10201511253468432",
      "bkpf678kefh5af8d",
      "144491230000411",
      "%E7%BA%A2%E5%8C%85%E9%80%81%E4%B8%8D%E5%81%9C",
      "WEIXIN",
      "osPHijpDqSLW_W3JkePk6hm-PXm0",
      "1024000",
      "20151231",
      "14200416001127",
      "3m44Afz5oP"
    )

    val secureKey = "SSSSSSSKKKKKKKKKK"

    val strA = (secureKey :: pList).sorted.mkString("")

    println(strA)

    val signature = generateSignature(pList, secureKey)

    println(signature)


    val s = "红包送不停"

    val s1 = URLEncoder.encode(s, "utf-8")

    val s2 = URLDecoder.decode(s1, "utf-8")

    println(s1)
    println(s2)




  }


}
