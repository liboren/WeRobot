package models.tables
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object SlickTables extends {
  val profile = slick.driver.MySQLDriver
} with SlickTables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait SlickTables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(tAutoresponse.schema, tGroup.schema, tGroupuser.schema, tKeywordresponse.schema, tScheduleresponse.schema, tSystemuser.schema, tUsercookie.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tAutoresponse
    *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
    *  @param restype Database column restype SqlType(INT)
    *  @param response Database column response SqlType(VARCHAR), Length(255,true)
    *  @param userid Database column userid SqlType(BIGINT)
    *  @param groupnickname Database column groupnickname SqlType(VARCHAR), Length(255,true)
    *  @param state Database column state SqlType(INT) */
  case class rAutoresponse(id: Long, restype: Int, response: String, userid: Long, groupnickname: String, state: Int)
  /** GetResult implicit for fetching rAutoresponse objects using plain SQL queries */
  implicit def GetResultrAutoresponse(implicit e0: GR[Long], e1: GR[Int], e2: GR[String]): GR[rAutoresponse] = GR{
    prs => import prs._
      rAutoresponse.tupled((<<[Long], <<[Int], <<[String], <<[Long], <<[String], <<[Int]))
  }
  /** Table description of table autoresponse. Objects of this class serve as prototypes for rows in queries. */
  class tAutoresponse(_tableTag: Tag) extends Table[rAutoresponse](_tableTag, "autoresponse") {
    def * = (id, restype, response, userid, groupnickname, state) <> (rAutoresponse.tupled, rAutoresponse.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(restype), Rep.Some(response), Rep.Some(userid), Rep.Some(groupnickname), Rep.Some(state)).shaped.<>({r=>import r._; _1.map(_=> rAutoresponse.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column restype SqlType(INT) */
    val restype: Rep[Int] = column[Int]("restype")
    /** Database column response SqlType(VARCHAR), Length(255,true) */
    val response: Rep[String] = column[String]("response", O.Length(255,varying=true))
    /** Database column userid SqlType(BIGINT) */
    val userid: Rep[Long] = column[Long]("userid")
    /** Database column groupnickname SqlType(VARCHAR), Length(255,true) */
    val groupnickname: Rep[String] = column[String]("groupnickname", O.Length(255,varying=true))
    /** Database column state SqlType(INT) */
    val state: Rep[Int] = column[Int]("state")

    /** Foreign key referencing tSystemuser (database name autoresponse_ibfk_1) */
    lazy val tSystemuserFk = foreignKey("autoresponse_ibfk_1", userid, tSystemuser)(r => r.userid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Index over (groupnickname) (database name autoresponse_fk_groupid) */
    val index1 = index("autoresponse_fk_groupid", groupnickname)
  }
  /** Collection-like TableQuery object for table tAutoresponse */
  lazy val tAutoresponse = new TableQuery(tag => new tAutoresponse(tag))

  /** Entity class storing rows of table tGroup
    *  @param groupid Database column groupid SqlType(BIGINT), AutoInc, PrimaryKey
    *  @param groupnickname Database column groupnickname SqlType(VARCHAR), Length(255,true)
    *  @param ownerid Database column ownerid SqlType(BIGINT)
    *  @param state Database column state SqlType(INT)
    *  @param headimgurl Database column headimgurl SqlType(VARCHAR), Length(255,true)
    *  @param groupunionid Database column groupunionid SqlType(VARCHAR), Length(255,true)
    *  @param membercount Database column membercount SqlType(INT) */
  case class rGroup(groupid: Long, groupnickname: String, ownerid: Long, state: Int, headimgurl: String, groupunionid: String, membercount: Int)
  /** GetResult implicit for fetching rGroup objects using plain SQL queries */
  implicit def GetResultrGroup(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rGroup] = GR{
    prs => import prs._
      rGroup.tupled((<<[Long], <<[String], <<[Long], <<[Int], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table group. Objects of this class serve as prototypes for rows in queries. */
  class tGroup(_tableTag: Tag) extends Table[rGroup](_tableTag, "group") {
    def * = (groupid, groupnickname, ownerid, state, headimgurl, groupunionid, membercount) <> (rGroup.tupled, rGroup.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(groupid), Rep.Some(groupnickname), Rep.Some(ownerid), Rep.Some(state), Rep.Some(headimgurl), Rep.Some(groupunionid), Rep.Some(membercount)).shaped.<>({r=>import r._; _1.map(_=> rGroup.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column groupid SqlType(BIGINT), AutoInc, PrimaryKey */
    val groupid: Rep[Long] = column[Long]("groupid", O.AutoInc, O.PrimaryKey)
    /** Database column groupnickname SqlType(VARCHAR), Length(255,true) */
    val groupnickname: Rep[String] = column[String]("groupnickname", O.Length(255,varying=true))
    /** Database column ownerid SqlType(BIGINT) */
    val ownerid: Rep[Long] = column[Long]("ownerid")
    /** Database column state SqlType(INT) */
    val state: Rep[Int] = column[Int]("state")
    /** Database column headimgurl SqlType(VARCHAR), Length(255,true) */
    val headimgurl: Rep[String] = column[String]("headimgurl", O.Length(255,varying=true))
    /** Database column groupunionid SqlType(VARCHAR), Length(255,true) */
    val groupunionid: Rep[String] = column[String]("groupunionid", O.Length(255,varying=true))
    /** Database column membercount SqlType(INT) */
    val membercount: Rep[Int] = column[Int]("membercount")

    /** Foreign key referencing tSystemuser (database name fk_ownerid) */
    lazy val tSystemuserFk = foreignKey("fk_ownerid", ownerid, tSystemuser)(r => r.userid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table tGroup */
  lazy val tGroup = new TableQuery(tag => new tGroup(tag))

  /** Entity class storing rows of table tGroupuser
    *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
    *  @param userunionid Database column userunionid SqlType(VARCHAR), Length(255,true)
    *  @param groupid Database column groupid SqlType(BIGINT)
    *  @param usernickname Database column usernickname SqlType(VARCHAR), Length(255,true)
    *  @param userdisplayname Database column userdisplayname SqlType(VARCHAR), Length(255,true) */
  case class rGroupuser(id: Long, userunionid: String, groupid: Long, usernickname: String, userdisplayname: String)
  /** GetResult implicit for fetching rGroupuser objects using plain SQL queries */
  implicit def GetResultrGroupuser(implicit e0: GR[Long], e1: GR[String]): GR[rGroupuser] = GR{
    prs => import prs._
      rGroupuser.tupled((<<[Long], <<[String], <<[Long], <<[String], <<[String]))
  }
  /** Table description of table groupuser. Objects of this class serve as prototypes for rows in queries. */
  class tGroupuser(_tableTag: Tag) extends Table[rGroupuser](_tableTag, "groupuser") {
    def * = (id, userunionid, groupid, usernickname, userdisplayname) <> (rGroupuser.tupled, rGroupuser.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userunionid), Rep.Some(groupid), Rep.Some(usernickname), Rep.Some(userdisplayname)).shaped.<>({r=>import r._; _1.map(_=> rGroupuser.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column userunionid SqlType(VARCHAR), Length(255,true) */
    val userunionid: Rep[String] = column[String]("userunionid", O.Length(255,varying=true))
    /** Database column groupid SqlType(BIGINT) */
    val groupid: Rep[Long] = column[Long]("groupid")
    /** Database column usernickname SqlType(VARCHAR), Length(255,true) */
    val usernickname: Rep[String] = column[String]("usernickname", O.Length(255,varying=true))
    /** Database column userdisplayname SqlType(VARCHAR), Length(255,true) */
    val userdisplayname: Rep[String] = column[String]("userdisplayname", O.Length(255,varying=true))

    /** Foreign key referencing tGroup (database name fk_groupid) */
    lazy val tGroupFk = foreignKey("fk_groupid", groupid, tGroup)(r => r.groupid, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)

    /** Index over (userunionid) (database name index_userunionid) */
    val index1 = index("index_userunionid", userunionid)
  }
  /** Collection-like TableQuery object for table tGroupuser */
  lazy val tGroupuser = new TableQuery(tag => new tGroupuser(tag))

  /** Entity class storing rows of table tKeywordresponse
    *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
    *  @param keyword Database column keyword SqlType(VARCHAR), Length(255,true)
    *  @param restype Database column restype SqlType(INT)
    *  @param response Database column response SqlType(VARCHAR), Length(255,true)
    *  @param triggertype Database column triggertype SqlType(INT)
    *  @param userid Database column userid SqlType(BIGINT)
    *  @param groupnickname Database column groupnickname SqlType(VARCHAR), Length(255,true)
    *  @param state Database column state SqlType(INT) */
  case class rKeywordresponse(id: Long, keyword: String, restype: Int, response: String, triggertype: Int, userid: Long, groupnickname: String, state: Int)
  /** GetResult implicit for fetching rKeywordresponse objects using plain SQL queries */
  implicit def GetResultrKeywordresponse(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rKeywordresponse] = GR{
    prs => import prs._
      rKeywordresponse.tupled((<<[Long], <<[String], <<[Int], <<[String], <<[Int], <<[Long], <<[String], <<[Int]))
  }
  /** Table description of table keywordresponse. Objects of this class serve as prototypes for rows in queries. */
  class tKeywordresponse(_tableTag: Tag) extends Table[rKeywordresponse](_tableTag, "keywordresponse") {
    def * = (id, keyword, restype, response, triggertype, userid, groupnickname, state) <> (rKeywordresponse.tupled, rKeywordresponse.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(keyword), Rep.Some(restype), Rep.Some(response), Rep.Some(triggertype), Rep.Some(userid), Rep.Some(groupnickname), Rep.Some(state)).shaped.<>({r=>import r._; _1.map(_=> rKeywordresponse.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column keyword SqlType(VARCHAR), Length(255,true) */
    val keyword: Rep[String] = column[String]("keyword", O.Length(255,varying=true))
    /** Database column restype SqlType(INT) */
    val restype: Rep[Int] = column[Int]("restype")
    /** Database column response SqlType(VARCHAR), Length(255,true) */
    val response: Rep[String] = column[String]("response", O.Length(255,varying=true))
    /** Database column triggertype SqlType(INT) */
    val triggertype: Rep[Int] = column[Int]("triggertype")
    /** Database column userid SqlType(BIGINT) */
    val userid: Rep[Long] = column[Long]("userid")
    /** Database column groupnickname SqlType(VARCHAR), Length(255,true) */
    val groupnickname: Rep[String] = column[String]("groupnickname", O.Length(255,varying=true))
    /** Database column state SqlType(INT) */
    val state: Rep[Int] = column[Int]("state")

    /** Foreign key referencing tSystemuser (database name fk_userid1) */
    lazy val tSystemuserFk = foreignKey("fk_userid1", userid, tSystemuser)(r => r.userid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Index over (groupnickname) (database name keywordresponse_fk_groupid) */
    val index1 = index("keywordresponse_fk_groupid", groupnickname)
  }
  /** Collection-like TableQuery object for table tKeywordresponse */
  lazy val tKeywordresponse = new TableQuery(tag => new tKeywordresponse(tag))

  /** Entity class storing rows of table tScheduleresponse
    *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
    *  @param groupid Database column groupid SqlType(BIGINT)
    *  @param groupname Database column groupname SqlType(VARCHAR), Length(255,true)
    *  @param userid Database column userid SqlType(BIGINT)
    *  @param response Database column response SqlType(VARCHAR), Length(255,true)
    *  @param responsetype Database column responsetype SqlType(INT)
    *  @param state Database column state SqlType(INT)
    *  @param triggertime Database column triggertime SqlType(INT)
    *  @param triggerday Database column triggerday SqlType(INT) */
  case class rScheduleresponse(id: Long, groupid: Long, groupname: String, userid: Long, response: String, responsetype: Int, state: Int, triggertime: Int, triggerday: Int)
  /** GetResult implicit for fetching rScheduleresponse objects using plain SQL queries */
  implicit def GetResultrScheduleresponse(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rScheduleresponse] = GR{
    prs => import prs._
      rScheduleresponse.tupled((<<[Long], <<[Long], <<[String], <<[Long], <<[String], <<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table scheduleresponse. Objects of this class serve as prototypes for rows in queries. */
  class tScheduleresponse(_tableTag: Tag) extends Table[rScheduleresponse](_tableTag, "scheduleresponse") {
    def * = (id, groupid, groupname, userid, response, responsetype, state, triggertime, triggerday) <> (rScheduleresponse.tupled, rScheduleresponse.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(groupid), Rep.Some(groupname), Rep.Some(userid), Rep.Some(response), Rep.Some(responsetype), Rep.Some(state), Rep.Some(triggertime), Rep.Some(triggerday)).shaped.<>({r=>import r._; _1.map(_=> rScheduleresponse.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column groupid SqlType(BIGINT) */
    val groupid: Rep[Long] = column[Long]("groupid")
    /** Database column groupname SqlType(VARCHAR), Length(255,true) */
    val groupname: Rep[String] = column[String]("groupname", O.Length(255,varying=true))
    /** Database column userid SqlType(BIGINT) */
    val userid: Rep[Long] = column[Long]("userid")
    /** Database column response SqlType(VARCHAR), Length(255,true) */
    val response: Rep[String] = column[String]("response", O.Length(255,varying=true))
    /** Database column responsetype SqlType(INT) */
    val responsetype: Rep[Int] = column[Int]("responsetype")
    /** Database column state SqlType(INT) */
    val state: Rep[Int] = column[Int]("state")
    /** Database column triggertime SqlType(INT) */
    val triggertime: Rep[Int] = column[Int]("triggertime")
    /** Database column triggerday SqlType(INT) */
    val triggerday: Rep[Int] = column[Int]("triggerday")

    /** Foreign key referencing tSystemuser (database name fk_userid) */
    lazy val tSystemuserFk = foreignKey("fk_userid", userid, tSystemuser)(r => r.userid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Index over (groupid) (database name index_groupid) */
    val index1 = index("index_groupid", groupid)
  }
  /** Collection-like TableQuery object for table tScheduleresponse */
  lazy val tScheduleresponse = new TableQuery(tag => new tScheduleresponse(tag))

  /** Entity class storing rows of table tSystemuser
    *  @param userid Database column userid SqlType(BIGINT), AutoInc, PrimaryKey
    *  @param userunionid Database column userunionid SqlType(VARCHAR), Length(255,true)
    *  @param username Database column username SqlType(VARCHAR), Length(255,true)
    *  @param createtime Database column createtime SqlType(BIGINT)
    *  @param account Database column account SqlType(VARCHAR), Length(255,true)
    *  @param password Database column password SqlType(VARCHAR), Length(255,true) */
  case class rSystemuser(userid: Long, userunionid: String, username: String, createtime: Long, account: String, password: String)
  /** GetResult implicit for fetching rSystemuser objects using plain SQL queries */
  implicit def GetResultrSystemuser(implicit e0: GR[Long], e1: GR[String]): GR[rSystemuser] = GR{
    prs => import prs._
      rSystemuser.tupled((<<[Long], <<[String], <<[String], <<[Long], <<[String], <<[String]))
  }
  /** Table description of table systemuser. Objects of this class serve as prototypes for rows in queries. */
  class tSystemuser(_tableTag: Tag) extends Table[rSystemuser](_tableTag, "systemuser") {
    def * = (userid, userunionid, username, createtime, account, password) <> (rSystemuser.tupled, rSystemuser.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userid), Rep.Some(userunionid), Rep.Some(username), Rep.Some(createtime), Rep.Some(account), Rep.Some(password)).shaped.<>({r=>import r._; _1.map(_=> rSystemuser.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column userid SqlType(BIGINT), AutoInc, PrimaryKey */
    val userid: Rep[Long] = column[Long]("userid", O.AutoInc, O.PrimaryKey)
    /** Database column userunionid SqlType(VARCHAR), Length(255,true) */
    val userunionid: Rep[String] = column[String]("userunionid", O.Length(255,varying=true))
    /** Database column username SqlType(VARCHAR), Length(255,true) */
    val username: Rep[String] = column[String]("username", O.Length(255,varying=true))
    /** Database column createtime SqlType(BIGINT) */
    val createtime: Rep[Long] = column[Long]("createtime")
    /** Database column account SqlType(VARCHAR), Length(255,true) */
    val account: Rep[String] = column[String]("account", O.Length(255,varying=true))
    /** Database column password SqlType(VARCHAR), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table tSystemuser */
  lazy val tSystemuser = new TableQuery(tag => new tSystemuser(tag))

  /** Entity class storing rows of table tUsercookie
    *  @param userid Database column userid SqlType(BIGINT), PrimaryKey
    *  @param cookie Database column cookie SqlType(VARCHAR), Length(1024,true)
    *  @param uin Database column uin SqlType(VARCHAR), Length(255,true)
    *  @param createtime Database column createtime SqlType(BIGINT) */
  case class rUsercookie(userid: Long, cookie: String, uin: String, createtime: Long)
  /** GetResult implicit for fetching rUsercookie objects using plain SQL queries */
  implicit def GetResultrUsercookie(implicit e0: GR[Long], e1: GR[String]): GR[rUsercookie] = GR{
    prs => import prs._
      rUsercookie.tupled((<<[Long], <<[String], <<[String], <<[Long]))
  }
  /** Table description of table usercookie. Objects of this class serve as prototypes for rows in queries. */
  class tUsercookie(_tableTag: Tag) extends Table[rUsercookie](_tableTag, "usercookie") {
    def * = (userid, cookie, uin, createtime) <> (rUsercookie.tupled, rUsercookie.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userid), Rep.Some(cookie), Rep.Some(uin), Rep.Some(createtime)).shaped.<>({r=>import r._; _1.map(_=> rUsercookie.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column userid SqlType(BIGINT), PrimaryKey */
    val userid: Rep[Long] = column[Long]("userid", O.PrimaryKey)
    /** Database column cookie SqlType(VARCHAR), Length(1024,true) */
    val cookie: Rep[String] = column[String]("cookie", O.Length(1024,varying=true))
    /** Database column uin SqlType(VARCHAR), Length(255,true) */
    val uin: Rep[String] = column[String]("uin", O.Length(255,varying=true))
    /** Database column createtime SqlType(BIGINT) */
    val createtime: Rep[Long] = column[Long]("createtime")

    /** Foreign key referencing tSystemuser (database name fk_usercookie_userid) */
    lazy val tSystemuserFk = foreignKey("fk_usercookie_userid", userid, tSystemuser)(r => r.userid, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table tUsercookie */
  lazy val tUsercookie = new TableQuery(tag => new tUsercookie(tag))
}
