package db.psql
import play.api.libs.json._
import org.postgresql.util.PSQLException
import models._
import models.IncentiveFormats._
import models.ReviewFormats._
import models.ReviewProFormats._
import models.ProductFormats._
import models.QuestionFormats._
import db.Op
import db.PsqlSetup
import java.text.SimpleDateFormat


class Operations extends Op {
    val setup = new PsqlSetup
    val db = setup.db
    def getProduct = {
      var list = List[Product]()
      val conn = db.getConnection()
      val stmt = conn.createStatement()  
        try {
          val rs = stmt.executeQuery("Select * from product")
          while (rs.next()) {
            val productid = rs.getString("_id")
            val name = rs.getString("product_name")
            val brand = rs.getString("brand")
            val price = rs.getLong("price")
            val sales_url = rs.getString("sales_url")
            list = Product(pid= productid, brand= brand, name = name, price = price, sales_url=sales_url) :: list
          }
        } finally {   
          conn.close()
        }
        val result : JsValue = Json.toJson(list)        
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
              val review = Review(reviews = Json.obj(question -> answer).toString)
              list = review :: list
            }
          }
        } finally {   
          conn.close()
        }
        list
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

    def getIncentive = {
      var list = List[Incentive]()
      val conn = db.getConnection()
      val stmt = conn.createStatement()

      try {
        
        val rs   = stmt.executeQuery("Select * from incentive")

        while (rs.next()) {
          val iid = rs.getString("_id")
          val code = rs.getString("code")
          val sdate = rs.getDate("start_date")
          val edate = rs.getDate("end_date")
          val tc = rs.getString("tc")
          val condition = rs.getString("condition")
          val pid = rs.getString("product_id")
          list = Incentive(iid= iid, code= code, start_date= sdate, end_date = edate, tc=tc, condition=condition ,product_id=pid) :: list
        }
      } finally {   
        conn.close()
      }
      val result : JsValue = Json.toJson(list)
      result
    }

    def addIncentive(incentives: List[Incentive]): Unit = {
      val conn = db.getConnection()
      val stmt = conn.createStatement()

      incentives.map(incentive => {
        val iid = incentive.iid
        val code = incentive.code
        val start_date = incentive.start_date
        val end_date = incentive.end_date
        val tc = incentive.tc
        val condition = incentive.condition
        val product_id = incentive.product_id

        val rs = stmt.executeUpdate(s"INSERT INTO incentive(_id, code, start_date, end_date, tc, condition, product_id) VALUES('$iid' ,'$code', '$start_date', '$end_date', '$tc', '$condition' , '$product_id')")
        })
      conn.close()
    }

    def updateIncentive(incentives: List[Incentive]): Unit = {
        val conn = db.getConnection()
        val stmt = conn.createStatement()

        incentives.map(incentive => {
        val iid = incentive.iid
        val code = incentive.code
        val start_date = incentive.start_date
        val end_date = incentive.end_date
        val tc = incentive.tc
        val condition = incentive.condition
        val product_id = incentive.product_id

        val rs = stmt.executeUpdate(s"UPDATE incentive  SET _id = '$iid', code = '$code', start_date = '$start_date', end_date = '$end_date', tc = '$tc', condition = '$condition', product_id = '$product_id' WHERE _id = '$iid'")
        })
        conn.close()
    }

    def deleteIncentive(id: String): Boolean = {
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      val exist = stmt.executeQuery(s"select exists(select 1 from incentive where _id='$id')").next()
      if(exist) {
        val delete = stmt.executeUpdate(s"DELETE FROM incentive WHERE _id='$id'")
      }
      exist
    }

    def getReviewQuestion = {
      var list = List[Question]()
      val conn = db.getConnection()
      val stmt = conn.createStatement()

      try {
        val rs   = stmt.executeQuery("Select * from review_question")
        while (rs.next()) {
          val qid = rs.getString("_id")
          val question = rs.getString("question")
          val types = rs.getString("type")
        list = Question(qid= qid, question= question, types = types) :: list
        }
      } finally {   
        conn.close()
      }
      val result : JsValue = Json.toJson(list)
      result
    }

    def addReviewQuestion(questions: List[Question]): Unit = {
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      questions.map(question => {
        val qid = question.qid
        val ques = question.question
        val types = question.types

        val rs = stmt.executeUpdate(s"INSERT INTO review_question(_id, question, type) VALUES('$qid' ,'$ques', '$types')")
        })
      conn.close()
    }

    def updateReviewQuestion(questions: List[Question]): Unit = {
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      questions.map(question => {
        val qid = question.qid
        val ques = question.question
        val types = question.types

        val rs = stmt.executeUpdate(s"UPDATE review_question SET question = '$ques', type = '$types' WHERE _id = '$qid'")
        })      
        conn.close()
    }

    def deleteReviewQuestion(id: String): Boolean = {
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      val exist = stmt.executeQuery(s"select exists(select 1 from review_question where _id='$id')").next()
      if(exist){
        val delete = stmt.executeUpdate(s"DELETE FROM review_question WHERE _id='$id'")
      }
      exist
    }

    def getInfoQuestion = {
      var list = List[Question]()
      val conn = db.getConnection()
      val stmt = conn.createStatement()

      try {
        val rs   = stmt.executeQuery("Select * from user_question")
        while (rs.next()) {
          val qid = rs.getString("_id")
          val question = rs.getString("question")
          val types = rs.getString("type")
        list = Question(qid= qid, question= question, types = types) :: list
        }
      } finally {   
        conn.close()
      }
      val result : JsValue = Json.toJson(list)
      result
    }

    def addInfoQuestion(questions: List[Question]): Unit = {
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      questions.map(question => {
        val qid = question.qid
        val ques = question.question
        val types = question.types

        val rs = stmt.executeUpdate(s"INSERT INTO user_question(_id, question, type) VALUES('$qid' ,'$ques', '$types')")
        })
      conn.close()
    }

    def updateInfoQuestion(questions: List[Question]): Unit = {
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      questions.map(question => {
        val qid = question.qid
        val ques = question.question
        val types = question.types

        val rs = stmt.executeUpdate(s"UPDATE user_question SET question = '$ques', type = '$types' WHERE _id = '$qid'")
        })      
        conn.close()
    }

    def deleteInfoQuestion(id: String): Boolean = {
      val conn = db.getConnection()
      val stmt = conn.createStatement()
      val exist = stmt.executeQuery(s"select exists(select 1 from user_question where _id='$id')").next()
      if(exist){
        val delete = stmt.executeUpdate(s"DELETE FROM user_question WHERE _id='$id'")
      }
      exist
    }

    def productReviewAnalysis: Boolean = {
      true
    }

}