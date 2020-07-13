package controllers

import com.google.common.collect.ImmutableMap
import javax.inject._
import play.api.mvc._
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import com.google.api.gax.rpc.NotFoundException

// import 
// import db.Ehandler._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class ProductController @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  val app = new FirebaseSetup
  case class Question( question: String, types: String)
  case class Product(id: String, brand: String, name: String, price: Double, salesURL: String)

  implicit val ProductReads: Reads[Product] = Json.reads[Product]
  implicit val QuestionWrites  = Json.writes[Question]

  def getProduct = Action {
    var list = List[List[String]]()

    val querySnapshot = app.db.collection("user_question").get().get()
    val docs = querySnapshot.getDocuments()

    docs.forEach(doc => {
      val question = doc.getString("question")
      val types = doc.getString("type")
      list = List(question,types) :: list
    })

    val seclist = list.map(l => Question(question= l(0), types = l(1)))
    val result : JsValue = Json.toJson(seclist)
    Ok(result)
  }

 def addProduct = Action(parse.json) { request =>
 
  val result = Json.fromJson[List[Product]](request.body)
  
  result match {
    case JsSuccess(products: List[Product], path: JsPath) =>
      products.map(product => collect(product))

      def collect(product: Product) = {
        val db = app.db.collection("user_question").document(product.id)
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
            val db = app.db.collection("user_question").document(product.id)
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
  val exist = app.db.collection("user_question").document(id).get().get().exists()

  exist match {
    case true => {
      val db = app.db.collection("user_question").document(id).delete()
      Ok("data with id " + id + " is deleted") 
    }
    case _ => Results.Status(400)(id + " doesnt exist")
  }
 }
}
