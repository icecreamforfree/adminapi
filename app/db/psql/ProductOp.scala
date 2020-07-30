package db.psql
import play.api.libs.json._
import org.postgresql.util.PSQLException
import models._
import models.ProductFormats._
import db.Prod
import db.PsqlSetup

class ProductOp extends Prod {
    val setup = new PsqlSetup
    val db = setup.db

    def getProduct = {
      var list = List[List[String]]()
      val conn = db.getConnection()
      val stmt = conn.createStatement()  

        try {
          val rs = stmt.executeQuery("Select * from product")
          while (rs.next()) {
            val productid = rs.getString("_id")
            val name = rs.getString("product_name")
            val brand = rs.getString("brand")
            val price = rs.getLong("price").toString
            val sales_url = rs.getString("sales_url")
            list = List(productid, brand, name, price, sales_url) :: list
          }
        } finally {   
          conn.close()
        }
        val seclist = list.map(l => Product(pid= l(0), brand= l(1), name = l(2), price = l(3).toDouble, sales_url=l(4)))
        val result : JsValue = Json.toJson(seclist)        
        result
  }

  def getReview(id: String): List[Review] = {
    var list = List[Review]()

        val conn = db.getConnection()
        val stmt = conn.createStatement()

        try {
          val rs = stmt.executeQuery("Select * from review")
          while (rs.next()) {
            val productid = rs.getString("product_id")
            if(productid == id){
              val question = rs.getString("question_id")
              val answer = rs.getString("answer")
              val review = Review(reviews = Json.obj(question -> answer)).toString)
              list = review :: list
            }
          }
        } finally {   
          conn.close()
        }
        list
}
    def deleteProduct(id: String) : Boolean = {
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      val exist = stmt.executeQuery(s"select exists(select 1 from product where _id='$id')").next()
      if(exist) {        
          val delete = stmt.executeUpdate(s"DELETE FROM product WHERE _id='$id'")
          conn.close()
      }
      exist
    }

    def addProduct(products: List[Product]): Unit = {
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      products.map(product => {
        val productid = product.pid
        val brand = product.brand
        val name = product.name
        val price = product.price
        val sales_url = product.sales_url

        val rs = stmt.executeUpdate(s"INSERT INTO product(_id, product_name, brand, price, sales_url) VALUES('$productid' ,'$name', '$brand', $price, '$sales_url')")
        })
        conn.close()
    }
    def updateProduct(products: List[Product]): Unit ={
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      products.map(product => {
        val productid = product.pid
        val brand = product.brand
        val name = product.name
        val price = product.price
        val sales_url = product.sales_url

        val rs = stmt.executeUpdate(s"UPDATE product SET product_name = '$name', brand= '$brand', price = $price, sales_url= '$sales_url' WHERE _id='$productid'")
        })
        conn.close()
    }
}