package db.firestore
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import com.google.api.gax.rpc.NotFoundException
import models.ProductF
import models.ProductFFormats._
import models.Review
import models.ReviewFormats

import play.api.libs.json._
import db.Prod

class ProductOp extends Prod{
    val app = new FirebaseSetup
    def getProduct: JsValue = {
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

        val seclist = list.map(l => ProductF(pid= l(0), brand= l(1), name = l(2), price = l(3).toDouble, salesURL=l(4)))
        val result : JsValue = Json.toJson(seclist)
        result
    }

    def getReview(id : String): List[Review] = {
      var list = List[Review]()
      val querySnapshot = app.db.collection("review").get().get()
      val docs = querySnapshot.getDocuments()

      docs.forEach(doc => {
        val productid = doc.getString("product_id") 
        if(productid == id){
          val reviews = Review(reviews = doc.get("review answer").toString)
          list = reviews :: list
            }
          })
          list
        }
    def deleteProduct(id: String): Boolean = {
      val exist = app.db.collection("product").document(id).get().get().exists()
      if(exist == true){
          val db = app.db.collection("product").document(id).delete()
        }
      exist
    }
  }