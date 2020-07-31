package controllers

import com.google.common.collect.ImmutableMap
import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import com.google.api.gax.rpc.NotFoundException
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

    def getReviewQuestion = Action {
      val data = mainDB.getReviewQuestion
      Ok(data)
    }
  
    def addReviewQuestion = Action(parse.json) { request =>
    
      val result = Json.fromJson[List[Question]](request.body)
      
      result match {
        case JsSuccess(questions: List[Question], path: JsPath) =>
            try{
              val data = mainDB.addReviewQuestion(questions)
              Ok(Json.obj("status" -> "succeed", "operation" -> "add"))
            } catch   {
              //  Catches all types of error/exceptions
              //  Use case to handle known/possible errors and, e: Throwable for anything else

              case notFound: java.util.concurrent.ExecutionException => BadRequest("Doc not found")
              // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
              case e: Throwable =>{
                println("gsus test", e.getClass().getSimpleName())
                BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
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

    def updateReviewQuestion = Action(parse.json) { request =>
      val result = Json.fromJson[List[Question]](request.body)

      result match {
        case JsSuccess(questions: List[Question], path: JsPath) => 
            try{
              val data = mainDB.updateReviewQuestion(questions)
              Ok(Json.obj("status" -> "succeed", "operation" -> "update"))
            } catch   {
              //  Catches all types of error/exceptions
              //  Use case to handle known/possible errors and, e: Throwable for anything else

              case notFound: java.util.concurrent.ExecutionException => BadRequest("Doc not found")
              // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
              case e: Throwable =>{
                println("gsus test", e.getClass().getSimpleName())
                BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
            }
            
        case e @ JsError(_) =>
          var errorList = List[JsObject]()
          val errors = JsError.toJson(e).fields

          BadRequest(Json.obj("mes" -> JsError.toJson(e)))

          for (err <- errors){
            errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
          }

          Results.Status(405)(Json.obj("status" -> true ,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))  
          }

      }

    def deleteReviewQuestion(id: String) = Action { request =>
      val data = mainDB.deleteReviewQuestion(id)
      data match {
        case true => {
          Ok(Json.obj("status" -> "succeed", "operation" -> "delete"))
        }
        case _ => Results.Status(400)(id + " doesnt exist")
      }
    }
  }
