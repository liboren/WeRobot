package common

/**
  * Created by Macbook on 2017/4/13.
  */
object Constants {

  object WeixinAPI{
    object getUuid{//获取二维码uuid
      val baseUrl = "http://login.weixin.qq.com/jslogin"
      val appid = "wx782c26e4c19acffb"
    }
  }
  object Response {
    val STATE_OPEN = 1 //回复状态 开启
    val STATE_CLOSE = 0 // 回复状态关闭
    val ACCURATE_MATCH = 1 // 关键词精确匹配
    val VAGUE_MATCH = 0 // 关键词模糊匹配
  }
  object FilePath {
    val IMG_PATH = "C:\\Users\\Macbook\\Desktop\\img\\" //图片存储路径
    val EMOTION_PATH = "C:\\Users\\Macbook\\Desktop\\emotion\\" //表情存储路径
  }
}
