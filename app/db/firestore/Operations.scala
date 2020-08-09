package db.firestore
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
// import java.lang.Object
import com.google.api.gax.rpc.NotFoundException
import com.google.common.collect.ImmutableMap
import models._
import models.IncentiveFormats._
import models.ReviewFormats._
import models.ProductFormats._
import models.QuestionFormats._
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.Date
import play.api.libs.json._
import db.Op

class Operations extends Op{
    val app = new FirebaseSetup
    def getProduct: JsValue = {
        var list = List[Product]()

        val querySnapshot = app.db.collection("product").get().get()
        val docs = querySnapshot.getDocuments()

        docs.forEach(doc => {
            val productid = doc.getId()
            val name = doc.getString("name")
            val brand = doc.getString("brand")
            val price = doc.getDouble("price")
            val sales_url = doc.getString("sales_url")
            list = Product(pid= productid, brand= brand, name = name, price = price, sales_url=sales_url) :: list
        })
        val result : JsValue = Json.toJson(list)
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
        val p = new ImmutableMap.Builder[String,Any]()
        .put("brand", product.brand)
        .put("name" , product.name)
        .put("price", product.price)
        .put("sales_url", product.sales_url)
        .build()

        val add = db.set(p)
      }
    }

    def updateProduct(products: List[Product]): Unit = {
      products.map(product => collect(product))
      def collect(product: Product) = {
          val db = app.db.collection("user_question").document(product.pid)
          val p = new ImmutableMap.Builder[String, Object]()
          .put("brand", product.brand)
          .put("name" , product.name)
          .put("sales_url", product.sales_url)
          .build()

          val add = db.update(p)
          val add2 = db.update("price" , product.price)
      }
    }

    def deleteProduct(id: String): Boolean = {
      val exist = app.db.collection("user_question").document(id).get().get().exists()
      if(exist == true){
          val db = app.db.collection("user_question").document(id).delete()
        }
      exist
    }

    def getIncentive: JsValue = {
      var list = List[Incentive]()

      val querySnapshot = app.db.collection("incentive").get().get()
      val docs = querySnapshot.getDocuments()

      docs.forEach(doc => {
          val iid = doc.getId()
          val code = doc.getString("code")
          val sdate = doc.getDate("start_date") 
          val edate = doc.getDate("end_date")
          val tc = doc.getString("tc")
          val condition = doc.getString("condition")
          val pid = doc.getString("product_id")
          list = Incentive(iid= iid, code= code, start_date= sdate, end_date = edate, tc=tc, condition=condition ,product_id=pid) :: list
      })
      val result : JsValue = Json.toJson(list)
      result
    }

    def addIncentive(incentives: List[Incentive]): Unit = {
      incentives.map(incentive => collect(incentive))

      def collect(incentive: Incentive) = {
        val db = app.db.collection("incentive").document(incentive.iid)
        val p = new ImmutableMap.Builder[String,Any]()
        .put("code", incentive.code)
        .put("start_date" , incentive.start_date)
        .put("end_date", incentive.start_date)
        .put("tc", incentive.tc)
        .put("condition", incentive.condition)
        .put("product_id", incentive.product_id)
        .build()

        val add = db.set(p)
      }
    }

    def updateIncentive(incentives: List[Incentive]): Unit = {
        for(incentive <- incentives){
          val db = app.db.collection("incentive").document(incentive.iid)
          val p = new ImmutableMap.Builder[String, Object]()
          .put("code", incentive.code)
          .put("tc", incentive.tc)
          .put("condition", incentive.condition)
          .put("product_id", incentive.product_id)
          .build()
          val add = db.update(p)
          val add2 = db.update("start_date" , incentive.start_date)
          val add3 = db.update("end_date" , incentive.end_date)          
        }
      }
    
    def deleteIncentive(id: String): Boolean = {
      val exist = app.db.collection("incentive").document(id).get().get().exists()
      if(exist){
        val db = app.db.collection("incentive").document(id).delete()
      }
      exist
    }

    def getReviewQuestion: JsValue = {
      var list = List[Question]()
      val querySnapshot = app.db.collection("review_question").get().get()
      val docs = querySnapshot.getDocuments()

      docs.forEach(doc => {
        val qid = doc.getId()
        val question = doc.getString("question")
        val types = doc.getString("type")
        list = Question(qid= qid, question= question, types = types) :: list
      })
      val result : JsValue = Json.toJson(list)
      result
    }

    def addReviewQuestion(questions: List[Question]): Unit = {
      questions.map(question => collect(question))

      def collect(question: Question) = {
        val db = app.db.collection("review_question").document(question.qid)
        val p = new ImmutableMap.Builder[Object,Object]()
        .put("question", question.question)
        .put("type" , question.types)
        .build()

        val add = db.set(p)
      }
    }

    def updateReviewQuestion(questions: List[Question]): Unit = {
      for(question <- questions){
        val db = app.db.collection("review_question").document(question.qid)
        val p = new ImmutableMap.Builder[String, Object]()
        .put("question", question.question)
        .put("type" , question.types)
        .build()
        val add = db.update(p)
      }
    }

    def deleteReviewQuestion(id: String): Boolean = {
      val exist = app.db.collection("review_question").document(id).get().get().exists()
      if(exist){
        val db = app.db.collection("review_question").document(id).delete()
      }
      exist
    }

    def getInfoQuestion: JsValue = {
      var list = List[Question]()
      val querySnapshot = app.db.collection("user_question").get().get()
      val docs = querySnapshot.getDocuments()

      docs.forEach(doc => {
        val qid = doc.getId()
        val question = doc.getString("question")
        val types = doc.getString("type")
        list = Question(qid= qid, question= question, types = types) :: list
      })
      val result : JsValue = Json.toJson(list)
      result
    }

    def addInfoQuestion(questions: List[Question]): Unit = {
      questions.map(question => collect(question))

      def collect(question: Question) = {
        val db = app.db.collection("user_question").document(question.qid)
        val p = new ImmutableMap.Builder[Object,Object]()
        .put("question", question.question)
        .put("type" , question.types)
        .build()

        val add = db.set(p)
      }
    }

    def updateInfoQuestion(questions: List[Question]): Unit = {
      for(question <- questions){
        val db = app.db.collection("user_question").document(question.qid)
        val p = new ImmutableMap.Builder[String, Object]()
        .put("question", question.question)
        .put("type" , question.types)
        .build()
        val add = db.update(p)
      }
    }

    def deleteInfoQuestion(id: String): Boolean = {
      val exist = app.db.collection("user_question").document(id).get().get().exists()
      if(exist){
        val db = app.db.collection("user_question").document(id).delete()
      }
      exist
    }
  }