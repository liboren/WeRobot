import java.util.regex.Pattern
//TODO 新人邀请 "FromUserName":"@@131473cf33f36c70ea5a95ce6c359a9e35f32c0ffbddf2e59e242f6a823ff2fa","ToUserName":"@fb6dce95633e13ca08e966a6f9a34e3c","MsgType":10000,"Content":"\" <span class=\"emoji emoji1f338\"></span>卷卷卷<span class=\"emoji emoji1f338\"></span>\"邀请\"Hou$e\"加入了群聊
//val chehui = "@129260f862259b5abff0904ee8810664b36f64e74e9d580bd86cd4ba366227a2:<br/>&lt;sysmsg type=\"revokemsg\"&gt;&lt;revokemsg&gt;&lt;session&gt;6726457518@chatroom&lt;/session&gt;&lt;oldmsgid&gt;1069738445&lt;/oldmsgid&gt;&lt;msgid&gt;6071299654335939419&lt;/msgid&gt;&lt;replacemsg&gt;&lt;![CDATA[\"超！神罗天征！<span class=\"emoji emoji1f47e\"></span>\" 撤回了一条消息]]&gt;&lt;/replacemsg&gt;&lt;/revokemsg&gt;&lt;/sysmsg&gt;"
//val str = "window.code=200;window.redirect_uri=\"https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?ticket=ARiFc26pwMtnUC2PBuJalkaS@qrticket_0&uuid=Yaw97YVBXQ==&lang=zh_CN&scan=1492838576\";"
//val yaoqing = "你邀请\"方二二\"加入了群聊"
val jubao = "举报@李暴龙 "
val content = "&lt;msg fromusername=\"xugaochao2010\" encryptusername=\"v1_942b2e99cbc7a08ce834b7ea15dac57e8ed89ab510f96ba34413e1c8047f9a4c@stran"

//val tiren = "你将\"张亮亮\"移出了群聊"
//val pattern = Pattern.compile("""举报@(.*?) """)
//val pattern = Pattern.compile("""(.*?)邀请\"(.*?)\"加入了群聊""")
//val pattern = Pattern.compile(""".*?/oldmsgid&gt;&lt;msgid&gt;(\d*).*?撤回了一条消息.*?""")
//val pattern = Pattern.compile("""(.*?)将"(.*?)"移出了群聊""")
val pattern = Pattern.compile("""&lt;msg fromusername="(.*?)".*?""")
//val pattern = Pattern.compile("""window.code=(\d*);window.redirect_uri="(.*)";""")
//val pattern = Pattern.compile(""".*window.code *= *(\d*);|.*window.code *= *(\d*).*window.redirect_uri *= *"(.*)".*""")
val matcher = pattern.matcher(content)
val boolean = matcher.matches()
val inviter = matcher.group(1)
//val invitee = matcher.group(2)
//val invitee2 = matcher.group(3)

//val a = "sss.mp3"
//val b = a.split("\\.").last

