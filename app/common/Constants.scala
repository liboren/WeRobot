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
    val IMG_PATH = "/usr/tmp/img/" //图片存储路径
    val EMOTION_PATH = "/usr/tmp/emotion/" //表情存储路径
    val VIDEO_PATH = "/usr/tmp/video/" //视频存储路径
    val AUDIO_PATH = "/usr/tmp/voice/" //音频存储路径

//    val IMG_PATH = "C:\\Users\\Macbook\\Desktop\\img\\" //图片存储路径
//    val EMOTION_PATH = "C:\\Users\\Macbook\\Desktop\\emotion\\" //表情存储路径
//    val VIDEO_PATH = "C:\\Users\\Macbook\\Desktop\\video\\" //视频存储路径
//    val AUDIO_PATH = "C:\\Users\\Macbook\\Desktop\\voice\\" //音频存储路径
  }
  object FileType{
    val IMG_TYPE = "jpeg"
    val VIDEO_TYPE = "mp4"
    val AUDIO_TYPE = "mp3"
  }
}
