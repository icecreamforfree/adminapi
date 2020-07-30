package controllers

import javax.inject._
import play.api.mvc._
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import db.Prod
import models.Review._
import models.ReviewFormats._
// import db.psql._


@Singleton
class MainController @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {
    val db = System.getenv("DATABASE")
    var mainDB: Prod = _

    if(db == "PSQL") {
        import _root_.db.psql.ProductOp
        
        println("p")
        // mainDB = new ProductOp 
    }
    else if(db == "FIRESTORE") {
        import _root_.db.firestore.ProductOp
        import models.ProductF._
        import models.ProductFFormats._


        mainDB = new ProductOp 
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
  }
