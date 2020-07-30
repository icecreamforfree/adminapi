package controllers

import com.google.common.collect.ImmutableMap
import javax.inject._
import play.api.mvc._
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import com.google.api.gax.rpc.NotFoundException
import scala.collection.mutable.HashMap 
import java.util.Map
import scala.collection.JavaConverters._
import db.Prod
import models.Review._
import models.ReviewFormats._
import models.Product
import models.ProductFormats._


@Singleton
class MainController @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {
    // val db = System.getenv("DATABASE")
    val db = "PSQL"
    var mainDB: Prod = _

    if(db == "PSQL") {
        import _root_.db.psql.ProductOp
        mainDB = new ProductOp
        println("psql")
    }
    else if(db == "FIRESTORE") {
        import _root_.db.firestore.ProductOp
        mainDB = new ProductOp 
        println("firestore")
    }

    def getProduct = Action {
      val data = mainDB.getProduct
      Ok(data)
    }

    def getReview(id: String) = Action {request =>
      try {
        val data = mainDB.getReview(id)
        println(data)
        val empty = data.isEmpty
          empty match {
            case true => Results.Status(400)(id + " doesnt exist")
            case false => {
              val result: JsValue = Json.toJson(data)
              Ok(result)
            }
          }
      } catch   {
          //  Catches all types of error/exceptions
          //  Use case to handle known/possible errors and, e: Throwable for anything else

          case notFound: java.util.concurrent.ExecutionException => BadRequest("Doc not found")
          // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
          case e: Throwable =>{
            println("gsus test", e.getClass().getSimpleName())
            BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
         }

    } 

    def deleteProduct(id: String) = Action {
      val data = mainDB.deleteProduct(id)
      data match {
        case true => {
          Ok(Json.obj("status" -> "succeed", "operation" -> "delete"))
        }
        case _ => Results.Status(400)(id + " doesnt exist")
      }
    }

    def addProduct = Action(parse.json) { request =>
      val result = Json.fromJson[List[Product]](request.body)
      result match {
        case JsSuccess(products: List[Product], path: JsPath) =>
          val data = mainDB.addProduct(products)
          Ok(Json.obj("status" -> "succeed", "operation" -> "add"))
        case e @ JsError(_) =>
          var errorList = List[JsObject]()
          val errors = JsError.toJson(e).fields

          for (err <- errors){
            errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
          }
          Results.Status(405)(Json.obj("status" -> true,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))
          }
    }

      def updateProduct = Action(parse.json) { request =>
        val result = Json.fromJson[List[Product]](request.body)

        result match {
          case JsSuccess(products: List[Product], path: JsPath) => 
              try{
                val data = mainDB.updateProduct(products)
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
  }
