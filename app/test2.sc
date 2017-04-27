import java.util.regex.Pattern

import scala.util.Random

//val lizi = "&lt;msg&gt;&lt;emoji fromusername = \"23423asf\" tousername = \"filehelper\" type=\"2\" idbuffer=\"media:0_0\" md5=\"325e6f67bf0bf95b58ca1e0d7cc51821\" len = \"494186\" productid=\"\" androidmd5=\"325e6f67bf0bf95b58ca1e0d7cc51821\" androidlen=\"494186\" s60v3md5 = \"325e6f67bf0bf95b58ca1e0d7cc51821\" s60v3len=\"494186\" s60v5md5 = \"325e6f67bf0bf95b58ca1e0d7cc51821\" s60v5len=\"494186\" cdnurl = \"http://emoji.qpic.cn/wx_emoji/xmrm0xnjtnahibtFOjQ7ywClrojsNQYOPyickj9Yo7D0NEa16DBl5GJA/\" designerid = \"\" thumburl = \"\" encrypturl = \"http://emoji.qpic.cn/wx_emoji/CvEmibBOcGYhrj1gCOCs48L4OdgAtuuyJueljF440oic4r8j6KPRkDfw/\" aeskey= \"caee1393b6427c72e35fef94d277d00b\" width= \"85\" height= \"85\" &gt;&lt;/emoji&gt; &lt;gameext type=\"0\" content=\"0\" &gt;&lt;/gameext&gt;&lt;/msg&gt;"
//
//
//val m = """/md5\s?=\s?"(.*?)".*?cdnurl\s?=\s?"(.*?)"/"""
//val line = "md5=\"325e6f67bf0bf95b58ca1e0d7cc51821\""
//val line2 = "buffer=\"media:0_0\" md5=\"325e6f67bf0bf95b58ca1e0d7cc51821\"  s60v"
//
//val pattern = Pattern.compile(""".*md5\s?=\s?"(.*?)".*cdnurl\s?=\s?"(.*?)".*""")
//val matcher = pattern.matcher(lizi)
//val boolean = matcher.matches()
//val md5 = matcher.group(1)
//val cdnurl = matcher.group(2)

val random = Random.nextInt(10)