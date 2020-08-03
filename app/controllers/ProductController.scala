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
import models.Review._
import models.ReviewFormats._
import models.Product
import models.ProductFormats._


@Singleton
class ProductController @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
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

    def getProduct = Action {
      try {
        val data = mainDB.getProduct
        Ok(succeed("get product", data))
      } catch   {
          case e : PSQLException => BadRequest(exception("get product", Json.obj("description" -> "Doc not found")))
          case notFound: java.util.concurrent.ExecutionException => BadRequest(exception("get product", Json.obj("description" -> "Doc not found")))
          // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
          case e: Throwable =>{
            Results.Status(405)(exception("get product", Json.obj("description" ->e.getMessage)))}
         }
    }

    def getReview(id: String) = Action {request =>
      try {
        val data = mainDB.getReview(id)
        val empty = data.isEmpty
          empty match {
            case true => Results.Status(400)(id + " doesnt exist")
            case false => {
              val result: JsValue = Json.toJson(data)
              Ok(succeed("get review", result))
            }
          }
      } catch   {
          case notFound: java.util.concurrent.ExecutionException => BadRequest(exception("get review", Json.obj("description" -> "Doc not found")))
          // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
          case e: Throwable =>{
            println("gsus test", e.getClass().getSimpleName())
             Results.Status(405)(exception("get review", Json.obj("description" ->e.getMessage)))
            }
         }
    } 

    def deleteProduct(id: String) = Action {
      val data = mainDB.deleteProduct(id)
      data match {
        case true => {
        Ok(succeed("delete product",Json.parse("""{"id" : "id"}""") ))
        }
        case _ => Results.Status(400)(exception("delete product",Json.obj("description" -> "Id not found")))
      }
    }

    def addProduct = Action(parse.json) { request =>
      val result = Json.fromJson[List[Product]](request.body)
      result match {
        case JsSuccess(products: List[Product], path: JsPath) =>
          try{
            val data = mainDB.addProduct(products)
            Ok(succeed("add product", Json.parse("""{"id" : "*"}"""))) 
          }catch   {
            // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
            case e: Throwable =>{
              println("gsus test", e.getClass().getSimpleName())
              BadRequest(exception("add product", Json.obj("description" ->e.getMessage)))}
         }     
        case e @ JsError(_) =>
          var errorList = List[JsObject]()
          val errors = JsError.toJson(e).fields

          for (err <- errors){
            errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
          }
             Results.Status(405)(exception("add product", Json.obj("data" -> errorList)))
          }
    }

      def updateProduct = Action(parse.json) { request =>
        val result = Json.fromJson[List[Product]](request.body)

        result match {
          case JsSuccess(products: List[Product], path: JsPath) => 
              try{
                val data = mainDB.updateProduct(products)
                Ok(succeed("update product", Json.parse("""{"id" : "*"}"""))) 
              } catch   {
                // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
                case e: Throwable =>{
                  println("gsus test", e.getClass().getSimpleName())
                  BadRequest(exception("update product", Json.obj("description" ->e.getMessage)))}
              }
              
          case e @ JsError(_) =>
            var errorList = List[JsObject]()
            val errors = JsError.toJson(e).fields

            for (err <- errors){
              errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
            }

             Results.Status(405)(exception("update product", Json.obj("data" -> errorList)))
             }
        }
  }
