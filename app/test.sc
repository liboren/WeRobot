import java.util.regex.Pattern
//TODO 新人邀请 "FromUserName":"@@131473cf33f36c70ea5a95ce6c359a9e35f32c0ffbddf2e59e242f6a823ff2fa","ToUserName":"@fb6dce95633e13ca08e966a6f9a34e3c","MsgType":10000,"Content":"\" <span class=\"emoji emoji1f338\"></span>卷卷卷<span class=\"emoji emoji1f338\"></span>\"邀请\"Hou$e\"加入了群聊

val str = "window.code=200;window.redirect_uri=\"https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?ticket=ARiFc26pwMtnUC2PBuJalkaS@qrticket_0&uuid=Yaw97YVBXQ==&lang=zh_CN&scan=1492838576\";"
val yaoqing = "你邀请\"方二二\"加入了群聊"
val tiren = "你将\"方二二\"移出了群聊"
//val pattern = Pattern.compile("""(.*?)邀请\"(.*?)\"加入了群聊""")
//val pattern = Pattern.compile("""(.*?)将\"(.*?)\"移出了群聊""")
val pattern = Pattern.compile("""window.code=(\d*);window.redirect_uri="(.*)";""")
//val pattern = Pattern.compile(""".*window.code *= *(\d*);|.*window.code *= *(\d*).*window.redirect_uri *= *"(.*)".*""")
val matcher = pattern.matcher(str)
val boolean = matcher.matches()
val inviter = matcher.group(1)
val invitee = matcher.group(2)
val invitee2 = matcher.group(3)

