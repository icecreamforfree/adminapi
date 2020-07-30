package db.psql
import play.api.libs.json._
import org.postgresql.util.PSQLException
import models._
import models.ProductFormats._

class ProductOp {
    def getProduct = {
    // var list = List[List[String]]()
    // // val conn = db.getConnection()
    // val stmt = conn.createStatement()

    // try {
      
    //   val rs   = stmt.executeQuery("Select * from product")

    //   while (rs.next()) {
    //     val productid = rs.getString("_id")
    //     val name = rs.getString("product_name")
    //     val brand = rs.getString("brand")
    //     val price = rs.getLong("price").toString
    //     val sales_url = rs.getString("sales_url")
    //     list = List(productid, brand, name, price, sales_url) :: list
    //   }
    // } finally {   
    //   conn.close()
    // }
    // val seclist = list.map(l => Product(pid= l(0), brand= l(1), name = l(2), price = l(3).toDouble, sales_url=l(4)))
    // val result : JsValue = Json.toJson(seclist)
    val result = "result"
    result
  }
}