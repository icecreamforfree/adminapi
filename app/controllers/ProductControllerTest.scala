package controllers

import javax.inject._

import play.api.mvc._
import play.api.libs.json._
import org.mongodb.scala._
import scala.collection.immutable.IndexedSeq
import org.mongodb.scala.model._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.Filters._

import models._
import handle._

@Singleton
class ProductControllerTest @Inject() (cc: ControllerComponents) 
  extends AbstractController(cc) {
    case class Product(_id: String, brand: String, name: String, price: Double, salesURL: String)
    implicit val ProductWrites = Json.writes[Product]
    implicit val ProductReads: Reads[Product] = Json.reads[Product]
  // To directly connect to the default server localhost on port 27017
  // val mongoClient: MongoClient = MongoClient()

  // Use a Connection String
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")

  // // or provide custom MongoClientSettings
  // val settings: MongoClientSettings = MongoClientSettings.builder()
  //     .applyToClusterSettings(b => b.hosts(List(new ServerAddress("localhost")).asJava))
  //     .build()

  // val mongoClient2: MongoClient = MongoClient(settings)

  val database: MongoDatabase = mongoClient.getDatabase("robotcone")
  val collection: MongoCollection[Document] = database.getCollection("product")


  def addProduct = Action(parse.json) { request =>
    val result = Json.fromJson[Product](request.body)

    result match {
      case JsSuccess(product: Product, path: JsPath) =>


        // products.map(product => collect(product))

        // def collect(product: Product)= {
          val prod: Document = Document("_id" -> product._id, "name" -> product.name , "brand" -> product.brand , "price" -> product.price, "salesURL" -> product.salesURL)
          val observable: Observable[Completed] = collection.insertOne(prod)
          observable.subscribe(new Observer[Completed] {

            override def onNext(result: Completed): Unit = println("Inserted")

            override def onError(e: Throwable): Unit = println("Failed " + e.getMessage())

            override def onComplete(): Unit = println("Completed")
            })

            // MongoClient.close()
            
        // }
        Ok("ok")
      case e @ JsError(_) =>
        var errorList = List[JsObject]()
        val errors = JsError.toJson(e).fields

        for (err <- errors){
          errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
        }

        Results.Status(405)(Json.obj("status" -> true,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))

    }
    
  }

  // def updateProduct = Action(parse.json) { request =>
  //   val result = Json.fromJson[Product](request.body)

  //   result match {
  //     case JsSuccess(product: Product, path: JsPath) =>
  //       try {
  //         println(product._id , " " , product.brand)
  //         // val filter: Document = Document("_id" , product._id)
  //         // val data: Document = Document("brand" , product.brand)
  //         // val set: Document = Document("$set" , data)
  //         // val obs: SingleObservable[org.mongodb.scala.result.UpdateResult] = collection.updateOne(equal("name" , "New") , set("brand" , "brand"))
  //         // obs.subscribe(new SingleObservable[org.mongodb.scala.result.UpdateResult] {

  //         //   override def onNext(result: Completed): Unit = println("")

  //         //   override def onError(e: Throwable): Unit = println("Failed " + e.getMessage())

  //         //   override def onComplete(): Unit = println("Completed")
  //         //   })
  //         val prod: Document = Document("_id" -> product._id, "name" -> product.name , "brand" -> product.brand , "price" -> product.price, "salesURL" -> product.salesURL)

  //         collection.insertOne(prod).results()
  //         Ok("updated")
  //       } catch {
  //           case e: Throwable =>{
  //             println("gsus test", e.getClass().getSimpleName())
  //             BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
  //       }

  //     case e @ JsError(_) =>
  //       var errorList = List[JsObject]()
  //       val errors = JsError.toJson(e).fields

  //       BadRequest(Json.obj("mes" -> JsError.toJson(e)))

  //       for (err <- errors){
  //         errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
  //       }

  //       Results.Status(405)(Json.obj("status" -> true ,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))  
  //   }
  // }
  } 
