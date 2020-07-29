// package controllers

// import javax.inject._

// import play.api.mvc._
// import play.api.db._
// import play.api.libs.json._
// import org.postgresql.util.PSQLException

// @Singleton
// class PsqlIncentiveController @Inject() (cc: ControllerComponents, db: Database) extends AbstractController(cc) {

  
//   case class Incentive(iid: String, code: String, start_date: Date, end_date: Date, product_id: String)

//   implicit val IncentiveWrites = Json.writes[Incentive]
//   implicit val IncentiveReads: Reads[Incentive] = Json.reads[Incentive]


//   def getIncentive = Action {
//     var list = List[List[String]]()
//     val conn = db.getConnection()
//     val stmt = conn.createStatement()

//     try {
      
//       val rs   = stmt.executeQuery("Select * from incentive")

//       while (rs.next()) {
//         val iid = rs.getString("_id")
//         val code = rs.getString("code")
//         val sdate = rs.getDate("start_date")
//         val edate = rs.getDate("end_date")
//         val pid = rs.getString("product_id")
//         list = List(iid,code, sdate,edate,pid) :: list
//       }
//     } finally {   
//       conn.close()
//     }
//     val seclist = list.map(l => Incentive(iid= l(0), code= l(1),  start_date= l(2), end_date = l(3), product_id=l(4)))
//     val result : JsValue = Json.toJson(seclist)
//     Ok(result)
//   }

//   def addQuestion = Action(parse.json) { request =>
//     val result = Json.fromJson[List[Product]](request.body)
//     val conn = db.getConnection()
//     val stmt = conn.createStatement()

//     result match {
//       case JsSuccess(incentives: List[Incentive], path: JsPath) =>
//         try {
//         incentives.map(incentive => {
//         val iid = incentive.iid
//         val code = incentive.code
//         val start_date = incentive.start_date
//         val end_date = incentive.end_date
//         val product_id = incentive.product_id

//           val rs = stmt.executeUpdate(s"INSERT INTO product(_id, code, start_date, end_date, product_id) VALUES('$iid' ,'$code', '$start_date', $end_date, '$product_id')")
//           })
//         Ok("ok")
//         } catch {
//             case e : PSQLException => BadRequest(Json.obj("PSQL Error" -> e.getMessage))
//             case e : Throwable => {
//               println("gsus test", e.getClass())
//               BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
//             } finally {
//               conn.close()
//             }       
        
//          case e @ JsError(_) =>
//       var errorList = List[JsObject]()
//       val errors = JsError.toJson(e).fields

//       for (err <- errors){
//         errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
//       }
//       Results.Status(405)(Json.obj("status" -> true,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))
//       }
//     }  

//     def updateQuestion = Action(parse.json) { request =>
//     val result = Json.fromJson[List[Incentive]](request.body)
//     val conn = db.getConnection()
//     val stmt = conn.createStatement()

//     result match {
//       case JsSuccess(incentives: List[Incentive], path: JsPath) =>
//         try {
//         incentives.map(incentive => {
//         val iid = incentive.iid
//         val code = incentive.code
//         val start_date = incentive.start_date
//         val end_date = incentive.end_date
//         val product_id = incentive.product_id

//           val rs = stmt.executeUpdate(s"UPDATE incentive  SET _id = '$iid', code = '$code', start_date = '$start_date', end_date = '$end_date', product_id = '$product_id' WHERE _id = '$iid'")
//           })
//         Ok("ok")
//         } catch {
//             case e : PSQLException => BadRequest(Json.obj("PSQL Error" -> e.getMessage))
//             case e : Throwable => {
//               println("gsus test", e.getClass())
//               BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
//             } finally {
//               conn.close()
//             }       
        
//          case e @ JsError(_) =>
//       var errorList = List[JsObject]()
//       val errors = JsError.toJson(e).fields

//       for (err <- errors){
//         errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
//       }
//       Results.Status(405)(Json.obj("status" -> true,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))
//       }
//     } 



//     def deleteQuestion(id: String) = Action { request =>
//       val conn = db.getConnection()
//       val stmt = conn.createStatement()
//       val exist = stmt.executeQuery(s"select exists(select 1 from incentive where _id='$id')").next()
//       println(exist)
//       exist match {
//       case true => {
//         try {
//           val delete = stmt.executeUpdate(s"DELETE FROM incentive WHERE _id='$id'")
//           // val db = app.db.collection("product").document(id).delete()
//           Ok("data with id " + id + " is deleted") 
//           } catch {
//             case e : PSQLException => BadRequest(Json.obj("PSQL Error" -> e.getMessage))
//             case e : Throwable => {
//               println("gsus test", e.getClass())
//               BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
//               } finally {
//               conn.close()
//               }  
//             }
//       case false => Results.Status(400)(id + " doesnt exist")
//       }
//       }
//   }
