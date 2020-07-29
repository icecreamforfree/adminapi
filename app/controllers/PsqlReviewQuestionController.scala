package controllers

import javax.inject._

import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import org.postgresql.util.PSQLException

@Singleton
class PsqlReviewQuestionController @Inject() (cc: ControllerComponents, db: Database) extends AbstractController(cc) {

  
  case class Question(qid: String, question: String, types: String) 

  implicit val QuestionWrites  = Json.writes[Question]
  implicit val QuestionReads: Reads[Question] = Json.reads[Question]


  def getQuestion = Action {
    var list = List[List[String]]()
    val conn = db.getConnection()
    val stmt = conn.createStatement()

    try {
      
      val rs   = stmt.executeQuery("Select * from review_question")

      while (rs.next()) {
        val qid = rs.getString("_id")
        val question = rs.getString("question")
        val types = rs.getString("type")
        list = List(qid,question,types) :: list
      }
    } finally {   
      conn.close()
    }
    val seclist = list.map(l => Question(qid= l(0), question= l(1), types = l(2)))
    val result : JsValue = Json.toJson(seclist)
    Ok(result)
  }

  def addQuestion = Action(parse.json) { request =>
    val result = Json.fromJson[List[Question]](request.body)
    val conn = db.getConnection()
    val stmt = conn.createStatement()

    result match {
      case JsSuccess(questions: List[Question], path: JsPath) =>
        try {
        questions.map(question => {
          val qid = question.qid
          val ques = question.question
          val types = question.types

          val rs = stmt.executeUpdate(s"INSERT INTO review_question(_id, question, type) VALUES('$qid' ,'$ques', '$types')")
          })
      Ok(Json.obj("status" -> "succeed", "operation" -> "add"))
        } catch {
            case e : PSQLException => BadRequest(Json.obj("PSQL Error" -> e.getMessage))
            case e : Throwable => {
              println("gsus test", e.getClass())
              BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
            } finally {
              conn.close()
            }       
        
         case e @ JsError(_) =>
      var errorList = List[JsObject]()
      val errors = JsError.toJson(e).fields

      for (err <- errors){
        errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
      }
      Results.Status(405)(Json.obj("status" -> true,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))
      }
    }  

    def updateQuestion = Action(parse.json) { request =>
    val result = Json.fromJson[List[Question]](request.body)
    val conn = db.getConnection()
    val stmt = conn.createStatement()

    result match {
      case JsSuccess(questions: List[Question], path: JsPath) =>
        try {
        questions.map(question => {
          val qid = question.qid
          val ques = question.question
          val types = question.types

          val rs = stmt.executeUpdate(s"UPDATE review_question SET question = '$ques', type = '$types' WHERE _id = '$qid'")
          })
      Ok(Json.obj("status" -> "succeed", "operation" -> "update"))
        } catch {
            case e : PSQLException => BadRequest(Json.obj("PSQL Error" -> e.getMessage))
            case e : Throwable => {
              println("gsus test", e.getClass())
              BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
            } finally {
              conn.close()
            }       
        
         case e @ JsError(_) =>
      var errorList = List[JsObject]()
      val errors = JsError.toJson(e).fields

      for (err <- errors){
        errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
      }
      Results.Status(405)(Json.obj("status" -> true,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))
      }
    } 



    def deleteQuestion(id: String) = Action { request =>
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      val exist = stmt.executeQuery(s"select exists(select 1 from review_question where _id='$id')").next()
      println(exist)
      exist match {
      case true => {
        try {
          val delete = stmt.executeUpdate(s"DELETE FROM review_question WHERE _id='$id'")
          // val db = app.db.collection("product").document(id).delete()
      Ok(Json.obj("status" -> "succeed", "operation" -> "delete"))
          } catch {
            case e : PSQLException => BadRequest(Json.obj("PSQL Error" -> e.getMessage))
            case e : Throwable => {
              println("gsus test", e.getClass())
              BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
              } finally {
              conn.close()
              }  
            }
      case false => Results.Status(400)(id + " doesnt exist")
      }
      }
  }
