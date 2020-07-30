package db.firestore
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import com.google.api.gax.rpc.NotFoundException
import models.Product
import models.ProductFormats._
import models.Review
import models.ReviewFormats
import com.google.common.collect.ImmutableMap


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
            val sales_url = doc.getString("sales_url")
            list = List(productid, brand, name, price, sales_url) :: list
        })

        val seclist = list.map(l => Product(pid= l(0), brand= l(1), name = l(2), price = l(3).toDouble, sales_url=l(4)))
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

    def addProduct(products: List[Product]): Unit = {
      products.map(product => collect(product))

      def collect(product: Product) = {
        val db = app.db.collection("user_question").document(product.pid)
        val p = new ImmutableMap.Builder[Object,Object]()
        .put("brand", product.brand)
        .put("name" , product.name)
        .put("price", product.price.toString)
        .put("sales_url", product.sales_url)
        .build()

        val add = db.set(p)
      }
    }

    def updateProduct(products: List[Product]): Unit = {
      for(product <- products){
        val db = app.db.collection("user_question").document(product.pid)
        val p = new ImmutableMap.Builder[String, Object]()
        .put("brand", product.brand)
        .put("name" , product.name)
        .put("price", product.price.toString)
        .put("sales_url", product.sales_url)
        .build()
        val add = db.update(p)
      }
    }

    def deleteProduct(id: String): Boolean = {
      val exist = app.db.collection("product").document(id).get().get().exists()
      if(exist == true){
          val db = app.db.collection("product").document(id).delete()
        }
      exist
    }
  }