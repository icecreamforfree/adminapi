package db
// import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import com.google.api.gax.rpc.NotFoundException

class Prod {
    
    // def getProduct: JsValue = {
    //     var list = List[List[String]]()

    //     val querySnapshot = app.db.collection("product").get().get()
    //     val docs = querySnapshot.getDocuments()

    //     docs.forEach(doc => {
    //     val productid = doc.getId()
    //         val name = doc.getString("product_name")
    //         val brand = doc.getString("brand")
    //         val price = doc.get("price").toString
    //         val salesURL = doc.getString("salesURL")
    //         list = List(productid, brand, name, price, salesURL) :: list
    //     })

    //     val seclist = list.map(l => Product(pid= l(0), brand= l(1), name = l(2), price = l(3).toDouble, salesURL=l(4)))
    //     val result : JsValue = Json.toJson(seclist)
    // }
    val d = "ds"
}