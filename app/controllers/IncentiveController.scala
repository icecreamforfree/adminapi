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
import models.Incentive
import models.IncentiveFormats._


@Singleton
class IncentiveController @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
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

    def getIncentive = Action {
      try {
        val data = mainDB.getIncentive
        Ok(succeed("get incentive", data))
      } catch   {
          case e : PSQLException => BadRequest(exception("get incentive", Json.obj("description" -> "Doc not found")))
          case notFound: java.util.concurrent.ExecutionException => BadRequest(exception("get product", Json.obj("description" -> "Doc not found")))
          // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
          case e: Throwable =>{
            Results.Status(405)(exception("get incentive", Json.obj("description" ->e.getMessage)))}
         }   
      }
    

    def addIncentive = Action(parse.json) { request =>
    
      val result = Json.fromJson[List[Incentive]](request.body)
      
      result match {
        case JsSuccess(incentives: List[Incentive], path: JsPath) =>
          try {
            val data = mainDB.addIncentive(incentives)
            Ok(succeed("add incentive", Json.parse("""{"id" : "*"}"""))) 
          
            } catch   {
              // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
              case e: Throwable =>{
                println("gsus test", e.getClass().getSimpleName())
              BadRequest(exception("add incentive", Json.obj("description" ->e.getMessage)))}
            }
        case e @ JsError(_) =>
          var errorList = List[JsObject]()
          val errors = JsError.toJson(e).fields

          for (err <- errors){
            errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
          }

             Results.Status(405)(exception("add incentive", Json.obj("data" -> errorList)))
      }
    }

    def updateIncentive = Action(parse.json) { request =>
      val result = Json.fromJson[List[Incentive]](request.body)

      result match {
        case JsSuccess(incentives: List[Incentive], path: JsPath) => 
          try {
            val data = mainDB.updateIncentive(incentives)
            Ok(succeed("update incentive", Json.parse("""{"id" : "*"}"""))) 
          
            } catch   {
              case notFound: java.util.concurrent.ExecutionException => BadRequest(exception("update incentive", Json.obj("description" -> "Doc not found")))
              // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
              case e: Throwable =>{
                println("gsus test", e.getClass().getSimpleName())
                BadRequest(exception("update incentive", Json.obj("description" ->e.getMessage)))}
            }
            
        case e @ JsError(_) =>
          var errorList = List[JsObject]()
          val errors = JsError.toJson(e).fields

          for (err <- errors){
            errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
          }

             Results.Status(405)(exception("update incentive", Json.obj("data" -> errorList)))
          }

      }

    def deleteIncentive(id: String) = Action { request =>

      val data = mainDB.deleteIncentive(id)
      data match {
        case true => {
        Ok(succeed("delete product",Json.parse("""{"id" : "id"}""") ))
        }
        case _ => Results.Status(400)(exception("delete incentive",Json.obj("description" -> "Id not found")))
      }
    }
    }
