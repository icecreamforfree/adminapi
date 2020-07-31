package db.firestore
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import com.google.api.gax.rpc.NotFoundException
import com.google.common.collect.ImmutableMap
import models._
import models.IncentiveFormats._
import models.ReviewFormats._
import models.ProductFormats._
import models.QuestionFormats._

import play.api.libs.json._
import db.Op

class Operations extends Op{
    val app = new FirebaseSetup
    def getProduct: JsValue = {
        var list = List[List[String]]()

        val querySnapshot = app.db.collection("product").get().get()
        val docs = querySnapshot.getDocuments()

        docs.forEach(doc => {
        val productid = doc.getId()
            val name = doc.getString("name")
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

    def getIncentive: JsValue = {
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

    def addIncentive(incentives: List[Incentive]): Unit = {
      incentives.map(incentive => collect(incentive))

      def collect(incentive: Incentive) = {
        val db = app.db.collection("incentive").document(incentive.iid)
        val p = new ImmutableMap.Builder[Object,Object]()
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
          .put("start_date" , incentive.start_date)
          .put("end_date", incentive.start_date)
          .put("tc", incentive.tc)
          .put("condition", incentive.condition)
          .put("product_id", incentive.product_id)
          .build()
          val add = db.update(p)
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
      var list = List[List[String]]()
      val querySnapshot = app.db.collection("review_question").get().get()
      val docs = querySnapshot.getDocuments()

      docs.forEach(doc => {
        val qid = doc.getId()
        val question = doc.getString("question")
        val types = doc.getString("type")
        list = List(qid,question,types) :: list
      })

      val seclist = list.map(l => Question(qid= l(0), question= l(1), types = l(2)))
      val result : JsValue = Json.toJson(seclist)
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
      var list = List[List[String]]()
      val querySnapshot = app.db.collection("user_question").get().get()
      val docs = querySnapshot.getDocuments()

      docs.forEach(doc => {
        val qid = doc.getId()
        val question = doc.getString("question")
        val types = doc.getString("type")
        list = List(qid,question,types) :: list
      })

      val seclist = list.map(l => Question(qid= l(0), question= l(1), types = l(2)))
      val result : JsValue = Json.toJson(seclist)
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