package controllers

import javax.inject._

import play.api.mvc._
import play.api.libs.json._
import play.api.Logger
// import cn.playscala.mongo._
// import cn.playscala.mongo.codecs.macrocodecs.JsonFormat
// import play.api.libs.json.Format
// import cn.playscala.mongo.annotations._

import scala.concurrent.{ ExecutionContext, Future }

// Reactive Mongo imports
import reactivemongo.api.Cursor

import play.modules.reactivemongo.{ 
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}
// BSON-JSON conversions/collection
import reactivemongo.play.json.collection._
import reactivemongo.play.json.compat._

@Singleton
class ProductControllerMongo @Inject() (cc: ControllerComponents,  val reactiveMongoApi: ReactiveMongoApi) 
  extends AbstractController(cc) with MongoController with ReactiveMongoComponents {
  
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global  
  // implicit val productFormat = Json.format[Product]


  def collection : Future[JSONCollection] =
    database.map(_.collection[JSONCollection]("product"))

  import models._
  import models.JsonFormats._

  def addProduct = Action.async(parse.json) { request =>
    // request.body.validate[List[Product]].map(_ => Ok) 
    println(request.body.validate[Product])
    request.body.validate[Product].map{products =>
        // products.map{ prod => 
          collection.flatMap(_.insert.one(products)).map(error => Created)      
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }
//,projection= Option.empty[JsObject])
  // def getProduct = Action.async {
  //   println(collection)
  //   val cursor: Future[Cursor[Product]] = collection.map{
  //     _.find(Json.obj("brand" -> "maybelline"),projection= Option.empty[JsObject])
  //     .cursor[Product]()
  //   }
  //   val futureProductList : Future[List[Product]] = 
  //     cursor.flatMap(_.collect[List](-1, Cursor.FailOnError[List[Product]]()))

  //   futureProductList.map { products =>
  //     Ok(products.toString)
  //   }
  //       // gather all the JsObjects in a list
  //   // val futurePostsList: Future[List[Product]] = cursor.collect[List]()

  //   // val futurePostsJsonArray: Future[JsArray] = futurePostsList.map { prods =>
  //   //   Json.arr(prods)
  //   // }

  //   // // everything's ok! Let's reply with the array
  //   // futurePostsJsonArray.map { prods=>
  //   //   Ok(prods)
  //   // }
  //   }
  // } 
