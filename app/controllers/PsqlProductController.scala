package controllers

import javax.inject._

import play.api.mvc._
import play.api.db._
import play.api.libs.json._
import org.postgresql.util.PSQLException

@Singleton
class PsqlProductController @Inject() (cc: ControllerComponents, db: Database) extends AbstractController(cc) {

  
  case class Product(pid: String, brand: String, name: String, price: Double, sales_url: String)

  implicit val ProductWrites = Json.writes[Product]
  implicit val ProductReads: Reads[Product] = Json.reads[Product]


  def getProduct = Action {
    var list = List[List[String]]()
    val conn = db.getConnection()
    val stmt = conn.createStatement()

    try {
      
      val rs   = stmt.executeQuery("Select * from product")

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
    Ok(result)
  }

  def addProduct = Action(parse.json) { request =>
    val result = Json.fromJson[List[Product]](request.body)
    val conn = db.getConnection()
    val stmt = conn.createStatement()

    result match {
      case JsSuccess(products: List[Product], path: JsPath) =>
        try {
        products.map(product => {
          val productid = product.pid
          val brand = product.brand
          val name = product.name
          val price = product.price
          val sales_url = product.sales_url

          val rs = stmt.executeUpdate(s"INSERT INTO product(_id, product_name, brand, price, sales_url) VALUES('$productid' ,'$name', '$brand', $price, '$sales_url')")
          })
        Ok("ok")
        } catch {
            case e : PSQLException => BadRequest(Json.obj("PSQL Error" -> e.getMessage))
            case e : Throwable => {
              println("gsus test", e.getClass())
              BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
            } finally {
              conn.close()
            }       
        
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
    val conn = db.getConnection()
    val stmt = conn.createStatement()

    result match {
      case JsSuccess(products: List[Product], path: JsPath) =>
        try {
        products.map(product => {
          val productid = product.pid
          val brand = product.brand
          val name = product.name
          val price = product.price
          val sales_url = product.sales_url

          val rs = stmt.executeUpdate(s"UPDATE product SET product_name = '$name', brand= '$brand', price = $price, sales_url= '$sales_url' WHERE _id='$productid'")
          })
        Ok("ok")
        } catch {
            case e : PSQLException => BadRequest(Json.obj("PSQL Error" -> e.getMessage))
            case e : Throwable => {
              println("gsus test", e.getClass())
              BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
            } finally {
              conn.close()
            }       
        
         case e @ JsError(_) =>
      var errorList = List[JsObject]()
      val errors = JsError.toJson(e).fields

      for (err <- errors){
        errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
      }
      Results.Status(405)(Json.obj("status" -> true,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))
      }
    } 



    def deleteProduct(id: String) = Action { request =>
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      val exist = stmt.executeQuery(s"select exists(select 1 from product where _id='$id')").next()
      println(exist)
      exist match {
      case true => {
        try {
          val delete = stmt.executeUpdate(s"DELETE FROM product WHERE _id='$id'")
          // val db = app.db.collection("product").document(id).delete()
          Ok("data with id " + id + " is deleted") 
          } catch {
            case e : PSQLException => BadRequest(Json.obj("PSQL Error" -> e.getMessage))
            case e : Throwable => {
              println("gsus test", e.getClass())
              BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
              } finally {
              conn.close()
              }  
            }
      case false => Results.Status(400)(id + " doesnt exist")
      }
      }
  }
