package util

/**
  * User: liboren.
  * Date: 2017/1/11.
  * Time: 16:06.
  */
object ReplyUtil{
  def autoReply(content: String, keywordList:List[(String,String,Int)]):String = {

    //TODO 微信个人消息里content直接是发送的内容，如果是群消息前面会加上“@6aafc882c573e45a36eec31c4f9a45c9:<br/>”，代表发送的用户的id，后面跟内容
    var conReform = ""
    if(content.startsWith("@")) {//@6aafc882c573e45a36eec31c4f9a45c9:<br/>gs12580
      conReform = content.split("<br/>")(1)
    }
    else{
        conReform = content
    }
    val keywords = List(
      ("黑科技", "[愉快]", 1),
      ("黑科技怎么", "如果发现黑科技失效请拨打寮内服务电话（gs12580）[愉快]", 0),
      ("黑科技为什么", "如果发现黑科技失效请拨打寮内服务电话（gs12580）[愉快]", 0),
      ("黑科技没用", "如果发现黑科技失效请拨打寮内服务电话（gs12580）[愉快]", 0),
      ("黑科技失效", "如果发现黑科技失效请拨打寮内服务电话（gs12580）[愉快]", 0),
      ("黑科技没了", "如果发现黑科技失效请拨打寮内服务电话（gs12580）[愉快]", 0),
      ("黑科技还没", "如果发现黑科技失效请拨打寮内服务电话（gs12580）[愉快]", 0),
      ("失效怎么", "如果发现黑科技失效请拨打寮内服务电话（gs12580）[愉快]", 0),
      ("失效了", "如果发现黑科技失效请拨打寮内服务电话（gs12580）[愉快]", 0),
      ("黑科技呢", "如果发现黑科技失效请拨打寮内服务电话（gs12580）[愉快]", 0),
      ("gs12580", "恭喜你成功拨打出了寮内服务电话gs12580！——————1.黑科技故障报修请按（gs1）2.想为黑科技贡献一份力量请按（gs2）3.人工服务请按（gs0）4.For English service, please press(gs9) 5.日本のプレスサービス（gs8）", 1),
      ("gs0","如需人工帮助，请@李暴龙 并加上您需要咨询的问题[愉快]",1),
      ("gs8","おめでとう、あなたは成功したラオス電話サービスgs12580内にダイヤルアウトしている！——————1.ブラック＆故障修理プレス（gs1）2.黒にしたい＆プレス（gs2）3.ヒューマン・サービス、プレス（gs0）4.中国のサービスに貢献プレス（gs12580）",1),
      ("gs9","Congratulations on your success for call out gs12580！——————1.black science technology repair service please press (gs1)2.want to contribute to the black science technology please press (gs2)3.artificial service please press (gs0)4.for Chinese service please press (gs12580)",1),
      ("gs111","当然只能是端茶送水跪求他们点开链接（如http://yys.163.com/h5/time/page88/?share_page=be18e46ffb6b17aff4be117a286c3666）[捂脸]",1),
      ("gs222","黑科技队列名单：流星、抽抽、折扇、不二、大弟弟、阿宅、徐尼玛、花间、SPLUS、叉叉、麋鹿、qy",1),
      ("gs1","如发现黑科技失效（爱心不够10个或一直是0），原因可能是账号信息过期（过期时间6小时），需要黑科技队列中的微信号点开任意一个活动链接（如http://yys.163.com/h5/time/page88/?share_page=be18e46ffb6b17aff4be117a286c3666）（出现画面即可），即可更新账号信息[愉快]————————————————PS：如何让这些微信号点开链接？（请按gs111）有谁在名单中？（请按gs222）",1),
      ("gs2","贡献力量方法：加技师（李暴龙）微信，申请通过后附上自拍照一张，技师收到自拍照并审核通过后会回复一个二维码，用扫码的方式扫描该二维码即可参与到黑科技的队伍中[愉快]",1)
//      ("会长", "我只是一名技术工人，需要帮忙请打12580", 0)
    )
    keywords.foreach { keyword =>
      keyword._3 match {
        case 1 => // 精确匹配
          if (keyword._1 == conReform) {
            return keyword._2
          }
        case 0 => // 模糊匹配
          if (conReform.contains(keyword._1)) {
            return keyword._2
          }
      }
    }

    null
  }

}
