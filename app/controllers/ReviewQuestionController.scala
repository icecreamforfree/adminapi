package controllers

import com.google.common.collect.ImmutableMap
import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import com.google.api.gax.rpc.NotFoundException
import org.postgresql.util.PSQLException
import java.util.Map
import scala.collection.JavaConverters._
import db.Op
import models.Question
import models.QuestionFormats._

@Singleton
class ReviewQuestionController @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {
    // val db = System.getenv("DATABASE")
    val db = "FIRESTORE"
    var mainDB: Op = _

    if(db == "PSQL") {
        import _root_.db.psql.Operations
        mainDB = new Operations
        println("psql")
    }
    else if(db == "FIRESTORE") {
        import _root_.db.firestore.Operations
        mainDB = new Operations 
        println("firestore")
    }
    def exception(operation: String ,details: JsObject): JsObject = {
      val tobeReturned = Json.obj("status" -> "failed", "operation" -> operation , "details" -> details)
      tobeReturned
    }

    def succeed(operation: String, data: JsValue): JsObject = {
      val tobeReturned = Json.obj("status" -> "succeed" , "operation" -> operation, "data" -> data)
      tobeReturned
    }

    def getReviewQuestion = Action {
      try {
        val data = mainDB.getReviewQuestion
        Ok(succeed("get review question", data))
      } catch   {
          case e : PSQLException => BadRequest(exception("get review question", Json.obj("description" -> "Doc not found")))
          case notFound: java.util.concurrent.ExecutionException => BadRequest(exception("get review question", Json.obj("description" -> "Doc not found")))
          // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
          case e: Throwable =>{
            Results.Status(405)(exception("get review question", Json.obj("description" ->e.getMessage)))}
         }
    }
  
    def addReviewQuestion = Action(parse.json) { request =>
    
      val result = Json.fromJson[List[Question]](request.body)
      
      result match {
        case JsSuccess(questions: List[Question], path: JsPath) =>
            try{
              val data = mainDB.addReviewQuestion(questions)
              Ok(succeed("add review question", Json.parse("""{"id" : "*"}"""))) 
            } catch   {
              // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
              case e: Throwable =>{
                println("gsus test", e.getClass().getSimpleName())
                BadRequest(exception("add review question", Json.obj("description" ->e.getMessage)))}
            }
        case e @ JsError(_) =>
          var errorList = List[JsObject]()
          val errors = JsError.toJson(e).fields

          for (err <- errors){
            errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
          }

             Results.Status(405)(exception("add review question", Json.obj("data" -> errorList)))
      }
    }

    def updateReviewQuestion = Action(parse.json) { request =>
      val result = Json.fromJson[List[Question]](request.body)

      result match {
        case JsSuccess(questions: List[Question], path: JsPath) => 
            try{
              val data = mainDB.updateReviewQuestion(questions)
              Ok(succeed("update review question", Json.parse("""{"id" : "*"}"""))) 
            } catch   {
              case notFound: java.util.concurrent.ExecutionException => BadRequest(exception("update review question", Json.obj("description" -> "Doc not found")))
              // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
              case e: Throwable =>{
                println("gsus test", e.getClass().getSimpleName())
                BadRequest(exception("update review question", Json.obj("description" ->e.getMessage)))}
            }
            
        case e @ JsError(_) =>
          var errorList = List[JsObject]()
          val errors = JsError.toJson(e).fields

          for (err <- errors){
            errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
          }

             Results.Status(405)(exception("update review question", Json.obj("data" -> errorList)))
          }

      }

    def deleteReviewQuestion(id: String) = Action { request =>
      val data = mainDB.deleteReviewQuestion(id)
      data match {
        case true => {
        Ok(succeed("delete review question",Json.parse("""{"id" : "id"}""") ))
        }
        case _ => Results.Status(400)(exception("delete review question",Json.obj("description" -> "Id not found")))
      }
    }
  }
