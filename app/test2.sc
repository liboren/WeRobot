val url = "http://yys.163.com/h5/dongzhi/meishi34/?share_page=be18e46ffb6b17aff4be117a286c3666&amp;app_channel=app_store"
val bool = url.startsWith("http://yys.163.com/h5/dongzhi")
val sharePage = url.split("=")(1).split("&")(0)