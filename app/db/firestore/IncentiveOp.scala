package db.firestore
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import com.google.api.gax.rpc.NotFoundException
import models._
import models.IncentiveFormats._
import play.api.libs.json._



class IncentiveOp {
    // case class Product(pid: String, brand: String, name: String, price: Double, salesURL: String)
    // case class Review(reviews: String)

    // implicit val ProductWrites = Json.writes[Product]
    // implicit val ProductReads: Reads[Product] = Json.reads[Product]
    // implicit val ReviewWrites = Json.writes[Review]
    def test = {
        val d = "ds"
        d
    }

    val app = new FirebaseSetup
    def getIncentive: JsValue =  {
        var list = List[List[String]]()

        val querySnapshot = app.db.collection("incentive").get().get()
        val docs = querySnapshot.getDocuments()

        docs.forEach(doc => {
            val iid = doc.getId()
            val code = doc.getString("code")
            val sdate = doc.getString("start_date")
            val edate = doc.getString("end_date")
            val tc = doc.getString("tc")
            val condition = doc.getString("condition")
            val pid = doc.getString("product_id")
            list = List(iid,code, sdate,edate,tc,condition,pid) :: list
        })

        val seclist = list.map(l => Incentive(iid= l(0), code= l(1),  start_date= l(2), end_date = l(3), tc=l(4), condition=l(5) ,product_id=l(6)))
        val result : JsValue = Json.toJson(seclist)
        result
    }
}