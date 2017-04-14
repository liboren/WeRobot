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
  lazy val schema: profile.SchemaDescription = tKeywordresponse.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tKeywordresponse
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param keyword Database column keyword SqlType(VARCHAR), Length(255,true)
   *  @param restype Database column restype SqlType(INT)
   *  @param response Database column response SqlType(VARCHAR), Length(255,true)
   *  @param triggertype Database column triggertype SqlType(INT)
   *  @param userid Database column userid SqlType(BIGINT) */
  case class rKeywordresponse(id: Long, keyword: String, restype: Int, response: String, triggertype: Int, userid: Long)
  /** GetResult implicit for fetching rKeywordresponse objects using plain SQL queries */
  implicit def GetResultrKeywordresponse(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rKeywordresponse] = GR{
    prs => import prs._
    rKeywordresponse.tupled((<<[Long], <<[String], <<[Int], <<[String], <<[Int], <<[Long]))
  }
  /** Table description of table keywordresponse. Objects of this class serve as prototypes for rows in queries. */
  class tKeywordresponse(_tableTag: Tag) extends Table[rKeywordresponse](_tableTag, "keywordresponse") {
    def * = (id, keyword, restype, response, triggertype, userid) <> (rKeywordresponse.tupled, rKeywordresponse.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(keyword), Rep.Some(restype), Rep.Some(response), Rep.Some(triggertype), Rep.Some(userid)).shaped.<>({r=>import r._; _1.map(_=> rKeywordresponse.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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

    /** Index over (userid) (database name index_userid) */
    val index1 = index("index_userid", userid)
  }
  /** Collection-like TableQuery object for table tKeywordresponse */
  lazy val tKeywordresponse = new TableQuery(tag => new tKeywordresponse(tag))
}
