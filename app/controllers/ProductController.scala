package controllers

import com.google.common.collect.ImmutableMap
import javax.inject._
import play.api.mvc._
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._

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
      Ok("error " + JsError.toJson(e))
  }
 }

def insert = Action {
  Ok(views.html.test())
}
}
//  def test() = Action { request =>
//     val input = request.body.asFormUrlEncoded
//     input.map { args =>
//       val ques = args("question").head
//       val ty = args("type").head
//       Ok(s"$ques --- $ty")
//     }.getOrElse{
//       Ok("failed")
//     }
//  }
// }


