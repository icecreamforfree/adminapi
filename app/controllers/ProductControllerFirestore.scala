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
// import db.Ehandler._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class ProductControllerFirestore @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  val app = new FirebaseSetup
  case class Product(pid: String, brand: String, name: String, price: Double, salesURL: String)
  case class Review(reviews: String)

  implicit val ProductWrites = Json.writes[Product]
  implicit val ProductReads: Reads[Product] = Json.reads[Product]
  implicit val ReviewWrites = Json.writes[Review]

  def getProduct = Action {
    var list = List[List[String]]()

    val querySnapshot = app.db.collection("product").get().get()
    val docs = querySnapshot.getDocuments()

    docs.forEach(doc => {
      val productid = doc.getId()
        val name = doc.getString("product_name")
        val brand = doc.getString("brand")
        val price = doc.get("price").toString
        val salesURL = doc.getString("salesURL")
        list = List(productid, brand, name, price, salesURL) :: list
    })

    val seclist = list.map(l => Product(pid= l(0), brand= l(1), name = l(2), price = l(3).toDouble, salesURL=l(4)))
    val result : JsValue = Json.toJson(seclist)
    Ok(result)
  }
  
  //////// review with product id /////////
  // def getProductReview = Action { request =>
  //   var list = List[List[String]]()
  //   var answerList = List[Object]()
  //   // var productList = Map[String,Object]()
  //   val querySnapshot = app.db.collection("review").get().get()
  //   val docs = querySnapshot.getDocuments()

  //   docs.forEach(doc => {
 
  //     val product_id = doc.getString("product_id")
  //     val products = app.db.collection("product").document(product_id).get().get()
  //     // products.forEach(prod => {
  //     //   val data = prod.getId()
  //     //   println(data)
  //     // })
  //     val review_id = doc.getId().toString
  //     val reviews = doc.get("review answer").toString
  //     list = List(review_id,product_id, reviews) :: list      
  //   })
  //   val seclist = list.map(l=> Review(review_id = l(0).toString(), prod_id = l(1).toString(), reviews= l(2).toString()))
  //   val result : JsValue = Json.toJson(seclist)
  //   // println(result)

  //   Ok(result)
  // }

/////// reviews only
  def getReview(id : String) = Action { request =>
    var list = List[Review]()
    val querySnapshot = app.db.collection("review").get().get()
    val docs = querySnapshot.getDocuments()

    try{
      docs.forEach(doc => {
        val productid = doc.getString("product_id") 
        if(productid == id){
          val reviews = Review(reviews = doc.get("review answer").toString)
          list = reviews :: list
        }
          })
          val empty = list.isEmpty
          empty match {
            case true => Results.Status(400)(id + " doesnt exist")
            case false => {
              val result: JsValue = Json.toJson(list)
              Ok(result)
            }
          }
    }   catch   {
          //  Catches all types of error/exceptions
          //  Use case to handle known/possible errors and, e: Throwable for anything else

          case notFound: java.util.concurrent.ExecutionException => BadRequest("Doc not found")
          // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
          case e: Throwable =>{
            println("gsus test", e.getClass().getSimpleName())
            BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
         }
  }

 def addProduct = Action(parse.json) { request =>
 
  val result = Json.fromJson[List[Product]](request.body)
  
  result match {
    case JsSuccess(products: List[Product], path: JsPath) =>
      products.map(product => collect(product))

      def collect(product: Product) = {
        val db = app.db.collection("user_question").document(product.pid)
        val p = new ImmutableMap.Builder[Object,Object]()
        .put("brand", product.brand)
        .put("name" , product.name)
        .put("price", product.price.toString)
        .put("salesURL", product.salesURL)
        .build()

        val add = db.set(p)
      }
      Ok("succeed")
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
          for(product <- products){
            val db = app.db.collection("user_question").document(product.pid)
            val p = new ImmutableMap.Builder[String, Object]()
            .put("brand", product.brand)
            .put("name" , product.name)
            .put("price", product.price.toString)
            .put("salesURL", product.salesURL)
            .build()
            val add = db.update(p)
            println(add.get())
          }
          Ok("done")
      
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

 def deleteProduct(id: String) = Action { request =>
  val exist = app.db.collection("product").document(id).get().get().exists()

  exist match {
    case true => {
      val db = app.db.collection("product").document(id).delete()
      Ok("data with id " + id + " is deleted") 
    }
    case _ => Results.Status(400)(id + " doesnt exist")
  }
 }
}
